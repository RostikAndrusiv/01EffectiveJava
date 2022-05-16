package org.rostik.andrusiv.guava;

import com.google.common.cache.*;
import org.rostik.andrusiv.model.Entity;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO incapsulate, onRemoval in constructor
public class GuavaCache {

    private static final Logger logger = Logger.getLogger(GuavaCache.class.getName());

    RemovalListener<Integer, Entity> listener;

    public GuavaCache(RemovalListener<Integer, Entity> listener) {
        this.listener = listener;
    }

    private final Cache<Integer, Entity> cache = CacheBuilder.newBuilder()
            .initialCapacity(2)
            .maximumSize(5)
            .recordStats()
            .removalListener(listener)
            .expireAfterAccess(500, TimeUnit.MILLISECONDS)
            .build();

    public void put(int key, Entity value){
        this.cache.put(key, value);
    }

    public Entity getIfPresent(int key){
        return cache.getIfPresent(key);
    }

    public CacheStats stats(){
        return cache.stats();
    }

    public long size(){
        return cache.size();
    }

    public ConcurrentMap<Integer, Entity> asMap(){
        return cache.asMap();
    }

//    private static void onRemoval(RemovalNotification<Integer, Entity> notification) {
//        if (notification.wasEvicted()) {
//            String event = String.format("Removed entry: %s : %s; cause: %s ", notification.getKey(), notification.getValue(), notification.getCause());
//            logger.log(Level.INFO, event);
//        }
//    }

}
