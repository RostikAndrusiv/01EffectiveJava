package org.rostik.andrusiv.guava;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.rostik.andrusiv.model.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@Slf4j
public class GuavaCacheTest {
    private static final Logger logger = LoggerFactory.getLogger(GuavaCache.class.getName());

    @Test
    public void testCache() throws InterruptedException {
        RemovalListener<Integer, Entity> listener = new RemovalListener<Integer, Entity>() {
            @Override
            public void onRemoval(RemovalNotification<Integer, Entity> notification) {
                if (notification.wasEvicted()) {
                    String event = String.format("Removed entry: %s : %s; cause: %s ", notification.getKey(), notification.getValue(), notification.getCause());
                    logger.info(event);
                }
            }
        };

       // given
        GuavaCache cache = GuavaCache.builder()
                .initialCapacity(2)
                .expireAfterAccessInMillis(500)
                .recordStats(true)
                .maximumSize(5)
                .listener(listener)
                .build();
        //when
        cache.put(1, new Entity("one"));
        cache.put(2, new Entity("two"));
        cache.put(3, new Entity("three"));
        cache.put(4, new Entity("four"));
        cache.put(5, new Entity("five"));
        cache.put(6, new Entity("six"));
        cache.put(7, new Entity("seven"));
        //then
        assertEquals(5, cache.size());
        //when
        cache.getIfPresent(1);
        //then
        assertNull(cache.getIfPresent(1));
        assertNotNull(cache.getIfPresent(7));

        System.out.println(cache.stats());
        //when
        Thread.sleep(1000);
        //then
        System.out.println(cache.size());
        assertEquals(5, cache.size());
        System.out.println(cache.asMap());
    }
}