package org.rostik.andrusiv.lfu;

import lombok.Builder;
import lombok.Data;
import org.rostik.andrusiv.model.Entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

public class LfuCacheImpl implements LfuCache {
    //TODO logs in listener +
    //TODO hide this behavior in getData (increment frequency, update date) +
    //TODO Date from jdk8 +
    //TODO move stats to external class(create methods) +
    //TODO Concurrency +
    private final Map<Integer, CacheItem> cacheMap = new HashMap<>();

    RemovalListener removalListener;

    CacheStats cacheStats = new CacheStats();

    private StampedLock lock = new StampedLock();

    private final int capacity;
    private final boolean isTimeBased;
    private final long expiryInMillis;
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

    @Data
    static class CacheItem {
        private Entity data;
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

    @Data
    static class CacheStats {
        private int capacity;
        private int itemsInCache;
        private int numberOfEvictions;
        private long averageInsertionTime;
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
            LocalDateTime currentTime = LocalDateTime.now();
            long stamp = lock.writeLock();
            try{
                for (Map.Entry<Integer, CacheItem> entry : cacheMap.entrySet()) {
                    if (currentTime.isAfter(entry.getValue().lastAccessTime.plusSeconds(expiryInMillis/1000))) {
                        Optional.ofNullable(removalListener)
                                .ifPresent(listener -> listener.log(entry, RemovalCauseEnum.EXPIRED));
                        cacheMap.remove(entry.getKey());
                        cacheStats.numberOfEvictions++;
                    }
                }
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }

    @Override
    public void put(int key, Entity data) {
        long startTime = System.nanoTime();
        long stamp = lock.writeLock();
        try{
            if (cacheMap.containsKey((key))) {
                CacheItem temp = new CacheItem(data);
                cacheMap.put(key, temp);
            } else {
                if (!isFull()) {
                    CacheItem temp = new CacheItem(data);
                    cacheMap.put(key, temp);
                    numberOfTotalInsertedItems++;
                } else {
                    int entryKeyToBeRemoved = getLFUKey();
                    Optional.ofNullable(removalListener)
                            .ifPresent(listener -> listener.log(cacheMap.get(entryKeyToBeRemoved), RemovalCauseEnum.LFU));
                    cacheMap.remove(entryKeyToBeRemoved);
                    cacheStats.numberOfEvictions++;
                    CacheItem temp = new CacheItem(data);
                    cacheMap.put(key, temp);
                    numberOfTotalInsertedItems++;
                }
            }
        }  finally {
            lock.unlockWrite(stamp);
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        cacheStats.setAverageInsertionTime(calculateAvgSpentTime(executionTime));
    }

    @Override
    public Entity get(int key) {
        long stamp = lock.readLock();
        try{
            CacheItem item = cacheMap.get(key);
            if (item == null) {
                return null;
            }
            return item.getData();
        }finally {
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

    private long calculateAvgSpentTime(long methodExecutionTime) {
        return (cacheStats.averageInsertionTime * (numberOfTotalInsertedItems - 1) + methodExecutionTime) / numberOfTotalInsertedItems;
    }


    private boolean isFull() {
        return cacheMap.size() == capacity;
    }

    @Override
    public int size() {
        return cacheMap.size();
    }

    public CacheStats getStats(){
        cacheStats.setCapacity(capacity);
        cacheStats.setItemsInCache(cacheMap.size());
        return cacheStats;
    }

    @Override
    public void printStats(){
        System.out.println(getStats());
    }
}
