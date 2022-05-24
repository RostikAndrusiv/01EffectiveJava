package org.rostik.andrusiv.guava;

import com.google.common.cache.*;
import lombok.Builder;
import org.rostik.andrusiv.model.Entity;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

//TODO incapsulate, onRemoval in constructor
public class GuavaCache {
    int initialCapacity = -1;
    int maximumSize = -1;
    boolean recordStats;
    RemovalListener<Integer, Entity> listener;
    long expireAfterAccessInMillis = -1L;

    @Builder
    public GuavaCache(int initialCapacity,  int maximumSize, boolean recordStats, RemovalListener<Integer, Entity> listener, long expireAfterAccessInMillis) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if(recordStats){
            builder.recordStats();
        }
        cache = builder
                .maximumSize(maximumSize)
                .removalListener(listener)
                .initialCapacity(initialCapacity)
                .expireAfterAccess(expireAfterAccessInMillis, TimeUnit.SECONDS)
                .build();
    }

    private final Cache<Integer, Entity> cache;

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

}
