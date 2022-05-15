package org.rostik.andrusiv.guava;

import org.junit.Test;
import org.rostik.andrusiv.model.Entity;

import static org.junit.Assert.*;

public class GuavaCacheTest {

    @Test
    public void testCache() throws InterruptedException {

        GuavaCache.cache.put(1, new Entity("one"));
        GuavaCache.cache.put(2, new Entity("two"));
        GuavaCache.cache.put(3, new Entity("three"));
        GuavaCache.cache.put(4, new Entity("four"));
        GuavaCache.cache.put(5, new Entity("five"));
        GuavaCache.cache.put(6, new Entity("six"));
        GuavaCache.cache.put(7, new Entity("seven"));

        GuavaCache.cache.getIfPresent(1);
        GuavaCache.cache.getIfPresent(7);
        System.out.println(GuavaCache.cache.stats());
        Thread.sleep(10000L);
        System.out.println(GuavaCache.cache.size());
    }
}