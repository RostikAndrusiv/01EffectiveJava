package org.rostik.andrusiv.lfu;

import lombok.Builder;
import lombok.Getter;
import org.rostik.andrusiv.model.Entity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

//TODO books: clean code, clean architecture, refactoring
//TODO read about collections.sort and arrays.sort impl, look how binary search implemented on Java (if exist), difference between primitives and objects sort, memory usage insertion and merge sorting

public class LfuCacheImpl implements LfuCache {
    private final Map<Integer, CacheItem> cacheMap = new HashMap<>();
    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);

    private final RemovalListener removalListener;

    private final StampedLock lock = new StampedLock();

    private final int capacity;
    private final boolean isTimeBased;
    private final long expiryInMillis;
    private int hitCount;
    private int missCount;
    private int numberOfEvictions;
    private long averageInsertionTime;

    //used only for avgTime calc
    private int numberOfTotalInsertedItems = 0;

    @Builder
    public LfuCacheImpl(int capacity, boolean isTimeBased, long expiryInMillis, RemovalListener removalListener) {
        this.capacity = capacity;
        this.isTimeBased = isTimeBased;
        this.expiryInMillis = expiryInMillis;
        this.removalListener = removalListener;

        initialize();
    }

    private void initialize() {
        if (isTimeBased) {
            startCleaningThread();
        }
    }

    @Getter
    static class CacheItem {
        private final Entity data;
        private int frequency;
        private LocalDateTime lastAccessTime;

        private CacheItem(Entity data) {
            this.data = data;
            this.lastAccessTime = LocalDateTime.now();
        }

        public Entity getData() {
            this.frequency++;
            lastAccessTime = LocalDateTime.now();
            return data;
        }
    }

    private void startCleaningThread() {
        Runnable task = () -> cleanMap();
        ses.scheduleAtFixedRate(task, expiryInMillis / 5, expiryInMillis / 5, TimeUnit.MILLISECONDS);
    }

    private void cleanMap() {
        long stamp = lock.writeLock();
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            for (Map.Entry<Integer, CacheItem> entry : cacheMap.entrySet()) {
                if (currentTime.isAfter(entry.getValue().lastAccessTime.plusNanos(expiryInMillis * 1000))) {
                    Optional.ofNullable(removalListener)
                            .ifPresent(listener -> listener.onRemove(entry, RemovalCauseEnum.EXPIRED));
                    cacheMap.remove(entry.getKey());
                    numberOfEvictions++;
                }
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }


    @Override
    public void put(int key, Entity data) {
        long stamp = lock.writeLock();
        try {
            long startTime = System.nanoTime();
            if (!cacheMap.containsKey(key) && isFull()) {
                int entryKeyToBeRemoved = getLFUKey();
                Optional.ofNullable(removalListener)
                        .ifPresent(listener -> listener.onRemove(cacheMap.get(entryKeyToBeRemoved), RemovalCauseEnum.SIZE));
                cacheMap.remove(entryKeyToBeRemoved);
                numberOfEvictions++;
            }

            CacheItem temp = new CacheItem(data);
            cacheMap.put(key, temp);
            numberOfTotalInsertedItems++;
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            calculateAvgSpentTime(executionTime);
        } finally {
            lock.unlockWrite(stamp);
        }

    }

    @Override
    public Entity get(int key) {
        long stamp = lock.readLock();
        try {
            CacheItem item = cacheMap.get(key);
            if (item == null) {
                missCount++;
                return null;
            }
            hitCount++;
            return item.getData();
        } finally {
            lock.unlockRead(stamp);
        }
    }

    private int getLFUKey() {
        int key = 0;
        int minFreq = Integer.MAX_VALUE;

        for (Map.Entry<Integer, CacheItem> entry : cacheMap.entrySet()) {
            if (minFreq > entry.getValue().frequency) {
                key = entry.getKey();
                minFreq = entry.getValue().frequency;
            }
        }
        return key;
    }

    private void calculateAvgSpentTime(long methodExecutionTime) {
        averageInsertionTime = (averageInsertionTime * (numberOfTotalInsertedItems - 1) + methodExecutionTime) / numberOfTotalInsertedItems;
    }

    private boolean isFull() {
        return cacheMap.size() == capacity;
    }

    @Override
    public int size() {
        return readLock(cacheMap::size);
    }

    public boolean containsKey(int key) {
        return readLock(() -> cacheMap.containsKey(key));
    }

    public CacheStats getStats() {
        return readLock(() -> new CacheStats(size(), hitCount, missCount, numberOfEvictions, averageInsertionTime));
    }

    private void readLock(Runnable action) {
        long stamp = lock.readLock();
        try {
            action.run();
        } finally {
            lock.unlockRead(stamp);
        }
    }

    private <T> T readLock(Supplier<T> action) {
        long stamp = lock.readLock();
        try {
            return action.get();
        } finally {
            lock.unlockRead(stamp);
        }
    }
}
