package org.rostik.andrusiv.lfu;

import lombok.Builder;
import lombok.Getter;
import org.rostik.andrusiv.model.Entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

//TODO stats in external class
//TODO books: clean code, clean architecture, refactoring


public class LfuCacheImpl implements LfuCache {
    //TODO move stats to external class(create methods)
    //TODO Concurrency (thread scheduling)
    private final Map<Integer, CacheItem> cacheMap = new HashMap<>();

    private RemovalListener removalListener;

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
            new LfuCacheImpl.CleanerThread().start();
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

    private class CleanerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                cleanMap();
                try {
                    Thread.sleep(expiryInMillis / 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void cleanMap() {
            long stamp = lock.writeLock();
            try {
                LocalDateTime currentTime = LocalDateTime.now();
                for (Map.Entry<Integer, CacheItem> entry : cacheMap.entrySet()) {
                    if (currentTime.isAfter(entry.getValue().lastAccessTime.plusSeconds(expiryInMillis / 1000))) {
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

    //TODO add lock
    private boolean isFull() {
        synchronized (cacheMap) {
            return cacheMap.size() == capacity;
        }
    }

    //TODO add lock
    @Override
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    //TODO add lock
    public boolean containsKey(int key) {
        synchronized (cacheMap) {
            return cacheMap.containsKey(key);
        }
    }

    //TODO lock
    public CacheStats getStats() {
        long stamp = lock.readLock();
        try {
            //stats
            int itemsInCache = size();
            return new CacheStats(itemsInCache, hitCount, missCount, numberOfEvictions, averageInsertionTime);
        } finally {
            lock.unlockRead(stamp);
        }
    }
}
