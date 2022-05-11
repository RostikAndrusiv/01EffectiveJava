package org.rostik.andrusiv.lfu;

import lombok.Builder;
import lombok.Data;
import org.rostik.andrusiv.model.Entity;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LFUCacheSingleMap {

    Logger logger = Logger.getLogger(LFUCacheSingleMap.class.getName());

    private static final ConcurrentHashMap<Integer, CacheItem> cacheMap = new ConcurrentHashMap<>();

    private final int capacity;
    private boolean isTimeBased = false;
    private long expiryInMillis;
    private long avgInsertionTime = 0;
    private int numberOfEvictions = 0;
    //used only for avgTime calc
    private int numberOfTotalInsertedItems = 0;

    @Builder
    public LFUCacheSingleMap(int capacity, boolean isTimeBased, long expiryInMillis) {
        this.capacity = capacity;
        this.isTimeBased = isTimeBased;
        this.expiryInMillis = expiryInMillis;

        initialize();
    }

    private void initialize() {
        if (isTimeBased) {
            new LFUCacheSingleMap.CleanerThread().start();
        }
    }

    @Data
    class CacheItem {
        private Entity data;
        private int frequency;
        private Long lastAccessTime;

        private CacheItem() {
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

    public void put(int key, Entity data) {
        long startTime = System.nanoTime();
        if (cacheMap.containsKey((key))) {
            CacheItem temp = new CacheItem();
            temp.setData(data);
            temp.setFrequency(0);
            cacheMap.put(key, temp);
            return;
        }
        if (!isFull()) {
            CacheItem temp = new CacheItem();
            temp.setData(data);
            temp.setFrequency(0);

            cacheMap.put(key, temp);
            numberOfTotalInsertedItems++;
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            calculateAvgSpentTime(executionTime);
        } else {
            int entryKeyToBeRemoved = getLFUKey();
            String event = String.format("Removing item: %s, CAUSE: LFU", cacheMap.get(entryKeyToBeRemoved));
            logger.log(Level.INFO, event);
            cacheMap.remove(entryKeyToBeRemoved);
            numberOfEvictions++;

            CacheItem temp = new CacheItem();
            temp.setData(data);
            temp.setFrequency(0);

            cacheMap.put(key, temp);
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            numberOfTotalInsertedItems++;
            calculateAvgSpentTime(executionTime);
        }
    }

    public Entity get(int key) {
        if(cacheMap.get(key) == null){
            return null;
        } else {
            CacheItem item = cacheMap.get(key);
            item.setFrequency(item.getFrequency()+1);
            item.setLastAccessTime(new Date().getTime());
            return item.getData();
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
        avgInsertionTime = ((avgInsertionTime * (numberOfTotalInsertedItems - 1) + methodExecutionTime) / numberOfTotalInsertedItems);
    }


    private boolean isFull() {
        return cacheMap.size() == capacity;
    }

    public int size(){
        return cacheMap.size();
    }

    public ConcurrentMap<Integer, CacheItem> getCacheMap() {
        return cacheMap;
    }
    public String getStatistic() {
        return String.format("capacity : %s, items : %s,  number of evictions : %s,  average insertion time : %s", capacity, cacheMap.size(), numberOfEvictions, avgInsertionTime);
    }

    public int getNumberOfEvictions() {
        return numberOfEvictions;
    }
}
