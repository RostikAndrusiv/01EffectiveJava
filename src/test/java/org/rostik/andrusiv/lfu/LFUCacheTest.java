package org.rostik.andrusiv.lfu;

import junit.framework.TestCase;
import org.junit.Before;
import org.rostik.andrusiv.model.Entity;

public class LFUCacheTest extends TestCase {

    public void testCacheNotTimeBased() {
        LFUCache cache = LFUCache.builder().capacity(2).build();
        cache.put(1, new Entity("1"));
        cache.put(2, new Entity("2"));
        cache.get(1);
        cache.put(3, new Entity("3"));
        cache.get(2);
        cache.get(3);
        cache.put(4, new Entity("4"));
        cache.put(5, new Entity("5"));
        cache.put(6, new Entity("6"));
        cache.put(6, new Entity("six"));
        cache.get(2);
        cache.get(3);
        cache.get(4);
        assertEquals(2, cache.getCache().size());
        assertNull(cache.get(256));
        assertEquals(new Entity("six"), cache.get(6));
        assertEquals(4, cache.getNumberOfEvictions());
    }

    public void testCacheTimeBased() throws InterruptedException {
        LFUCache cache = LFUCache.builder().capacity(2).isTimeBased(true).expiryInMillis(500).build();
        cache.put(1, new Entity("1"));
        assertEquals(new Entity("1"), cache.get(1));
        assertEquals(1, cache.getCache().size());
        Thread.sleep(1000L);
        assertNull(cache.get(1));
        assertEquals(0, cache.getCache().size());
    }
}