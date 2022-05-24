package org.rostik.andrusiv.lfu;

import lombok.Getter;

@Getter
public final class CacheStats {
    private final int itemsInCache;
    private final int hitCount;
    private final int missCount;
    private final int numberOfEvictions;
    private final long averageInsertionTime;

    public CacheStats(int itemsInCache, int hitCount, int missCount, int numberOfEvictions, long averageInsertionTime) {
        this.itemsInCache = itemsInCache;
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.numberOfEvictions = numberOfEvictions;
        this.averageInsertionTime = averageInsertionTime;
    }
}
