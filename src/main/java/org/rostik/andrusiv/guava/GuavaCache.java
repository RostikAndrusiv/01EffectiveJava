package org.rostik.andrusiv.guava;

import com.google.common.cache.*;
import lombok.Builder;
import org.rostik.andrusiv.model.Entity;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


public class GuavaCache {

    private final Cache<Integer, Entity> cache;

    @Builder
    public GuavaCache(int initialCapacity, int maximumSize, boolean recordStats, RemovalListener<Integer, Entity> listener, long expireAfterAccessInMillis) {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (recordStats) {
            builder.recordStats();
        }
        if (Objects.nonNull(listener)) {
            builder.removalListener(listener);
        }
        cache = builder
                .maximumSize(maximumSize)
                .initialCapacity(initialCapacity)
                .expireAfterAccess(expireAfterAccessInMillis, TimeUnit.SECONDS)
                .build();
    }

    public void put(int key, Entity value) {
        this.cache.put(key, value);
    }

    public Entity getIfPresent(int key) {
        return cache.getIfPresent(key);
    }

    public CacheStats stats() {
        return cache.stats();
    }

    public long size() {
        return cache.size();
    }

    public ConcurrentMap<Integer, Entity> asMap() {
        return cache.asMap();
    }

}
