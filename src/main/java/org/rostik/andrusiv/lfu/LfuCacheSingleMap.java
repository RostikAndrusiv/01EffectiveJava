package org.rostik.andrusiv.lfu;

import lombok.Builder;
import lombok.Data;
import org.rostik.andrusiv.model.Entity;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LfuCacheSingleMap implements LFUCacheInterface {

    Logger logger = Logger.getLogger(LfuCacheSingleMap.class.getName());

    private final ConcurrentHashMap<Integer, CacheItem> cacheMap = new ConcurrentHashMap<>();

    private final int capacity;
    private boolean isTimeBased = false;
    private final long expiryInMillis;
    private long avgInsertionTime = 0;
    private int numberOfEvictions = 0;
    //used only for avgTime calc
    private int numberOfTotalInsertedItems = 0;

    @Builder
    public LfuCacheSingleMap(int capacity, boolean isTimeBased, long expiryInMillis) {
        this.capacity = capacity;
        this.isTimeBased = isTimeBased;
        this.expiryInMillis = expiryInMillis;

        initialize();
    }

    private void initialize() {
        if (isTimeBased) {
            new LfuCacheSingleMap.CleanerThread().start();
        }
    }

    //TODO Date from jdk8
    @Data
    static class CacheItem {
        private Entity data;
        private int frequency;
        private Long lastAccessTime;

        private CacheItem(Entity data) {
            this.data = data;
            this.lastAccessTime = new Date().getTime();
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
            long currentTime = new Date().getTime();
            for (Map.Entry<Integer, CacheItem> entry : cacheMap.entrySet()) {
                if (currentTime > (entry.getValue().lastAccessTime + expiryInMillis)) {
                    String event = String.format("Removing item: %s, CAUSE: expired", entry);
                    logger.log(Level.INFO, event);
                    cacheMap.remove(entry.getKey());
                    numberOfEvictions++;
                }
            }
        }
    }

    //TODO move stats to external class(create methods)
    @Override
    public void put(int key, Entity data) {
        long startTime = System.nanoTime();
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
                //TODO move logging to listener
                String event = String.format("Removing item: %s, CAUSE: LFU", cacheMap.get(entryKeyToBeRemoved));
                logger.log(Level.INFO, event);
                cacheMap.remove(entryKeyToBeRemoved);
                numberOfEvictions++;
                CacheItem temp = new CacheItem(data);
                cacheMap.put(key, temp);
                numberOfTotalInsertedItems++;
            }
        }
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        calculateAvgSpentTime(executionTime);
    }

    @Override
    public Entity get(int key) {
        CacheItem item = cacheMap.get(key);
        if (item == null) {
            return null;
        }
        //TODO hide this behavior in getData
        item.setFrequency(item.getFrequency() + 1);
        item.setLastAccessTime(new Date().getTime());
        return item.getData();
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
        avgInsertionTime = ((avgInsertionTime * (numberOfTotalInsertedItems - 1) + methodExecutionTime) / numberOfTotalInsertedItems);
    }


    private boolean isFull() {
        return cacheMap.size() == capacity;
    }

    @Override
    public int size() {
        return cacheMap.size();
    }

    //TODO move into external class
    @Override
    public String getStatistic() {
        return String.format("capacity : %s, items : %s,  number of evictions : %s,  average insertion time : %s", capacity, cacheMap.size(), numberOfEvictions, avgInsertionTime);
    }

    @Override
    public int getNumberOfEvictions() {
        return numberOfEvictions;
    }
}
