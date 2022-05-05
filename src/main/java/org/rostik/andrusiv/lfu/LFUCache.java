package org.rostik.andrusiv.lfu;

import lombok.Builder;
import org.rostik.andrusiv.model.Entity;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LFUCache {
    Logger logger = Logger.getLogger(LFUCache.class.getName());

    Map<Integer, CacheItem> cache = new ConcurrentHashMap<>();
    Map<Integer, Integer> keyCounts = new ConcurrentHashMap<>();
    Map<Integer, LinkedHashSet<Integer>> frequencyMap = new ConcurrentHashMap<>();

    private final int capacity;
    private boolean isTimeBased = false;
    private final long expiryInMillis;

    private int minUsedValue = -1;
    private long avgInsertionTime = 0;
    private int numberOfEvictions = 0;
    //used only for avgTime calc
    private int numberOfTotalInsertedItems = 0;

    @Builder
    public LFUCache(int capacity, boolean isTimeBased, long expiryInMillis) {
        this.capacity = capacity;
        this.isTimeBased = isTimeBased;
        this.expiryInMillis = expiryInMillis;

        initialize();
    }

    private void initialize() {
        frequencyMap.put(1, new LinkedHashSet<>());
        if (isTimeBased) {
            new CleanerThread().start();
        }
    }

    public Entity get(int key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        // Get the count from counts map
        int count = keyCounts.get(key);
        // increase the counter
        keyCounts.put(key, count + 1);
        // remove the element from the counter to linked hashset
        frequencyMap.get(count).remove(key);
        if (count == minUsedValue && frequencyMap.get(count).isEmpty())
            minUsedValue++;
        //create new linked Hashset if there are no such key in frequencyMap
        if (!frequencyMap.containsKey(count + 1)) {
            frequencyMap.put(count + 1, new LinkedHashSet<>());
        }
        frequencyMap.get(count + 1).add(key);
        //update LastAccessTime
        cache.get(key).setInsertionTime(new Date().getTime());
        return cache.get(key).getData();
    }

    public void put(int key, Entity data) {
        long startTime = System.nanoTime();
        //replace entity in cache by new one
        if (cache.containsKey((key))) {
            cache.put(key, new CacheItem(data));
            //increase counter by calling get
            get(key);
            return;
        }
        // if cache is full just get first element from frequency map and delete everywhere by its id
        if (cache.size() >= capacity) {
            int evict = frequencyMap.get(minUsedValue).iterator().next();
            String event = String.format("Removing item with key: %s : %s", evict, cache.get(evict));
            logger.log(Level.INFO, event);
            frequencyMap.get(minUsedValue).remove(evict);
            cache.remove(evict);
            keyCounts.remove(evict);
            numberOfEvictions++;
        }
        cache.put(key, new CacheItem(data));
        keyCounts.put(key, 1);
        minUsedValue = 1;
        // using optional here, too lazy to write test for null case :)
        Optional.ofNullable(frequencyMap.get(1)).ifPresent(count -> count.add(key));

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        numberOfTotalInsertedItems++;
        calculateAvgSpentTime(executionTime);

    }

    //created wrapper over entity, so i could store time of insertion/last access
    public class CacheItem {
        private final Entity entity;
        private Long insertionTime;

        private CacheItem(Entity data) {
            this.entity = data;
            this.insertionTime = new Date().getTime();
        }

        @Override
        public String toString() {
            return "CacheItem{" +
                    "data=" + entity +
                    ", insertionTime=" + insertionTime +
                    '}';
        }

        public Entity getData() {
            return entity;
        }

        public void setInsertionTime(Long insertionTime) {
            this.insertionTime = insertionTime;
        }
    }

    private class CleanerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                cleanMap();
                try {
                    Thread.sleep(expiryInMillis / 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void cleanMap() {
        long currentTime = new Date().getTime();
//        cache.entrySet().removeIf(cacheItemEntry -> currentTime > (cacheItemEntry.getValue().insertionTime + expiryInMillis));
        for (Map.Entry<Integer, CacheItem> entry : cache.entrySet()) {
            if (currentTime > (entry.getValue().insertionTime + expiryInMillis)) {

                String event = String.format("Removing item with key: %s : %s", entry.getKey(), entry.getValue());
                logger.log(Level.INFO, event);
                frequencyMap.get(minUsedValue).remove(entry.getKey());
                cache.remove(entry.getKey());
                keyCounts.remove(entry.getKey());
                numberOfEvictions++;
            }
        }
    }

    private void calculateAvgSpentTime(long methodExecutionTime) {
        avgInsertionTime = ((avgInsertionTime * (numberOfTotalInsertedItems - 1) + methodExecutionTime) / numberOfTotalInsertedItems);
    }

    public int getNumberOfEvictions() {
        return numberOfEvictions;
    }

    public long getAvgInsertionTime() {
        return avgInsertionTime;
    }

    // cant test this method, avg time is not const
    public String getStatistic() {
        return String.format("number of evictions : %s,  average insertion time : %s", numberOfEvictions, avgInsertionTime);
    }

    // need this getter for testing :/
    public Map<Integer, CacheItem> getCache() {
        return cache;
    }


}
