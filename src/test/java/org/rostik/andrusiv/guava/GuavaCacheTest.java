package org.rostik.andrusiv.guava;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.junit.Test;
import org.rostik.andrusiv.model.Entity;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class GuavaCacheTest {

    @Test
    public void testCache() throws InterruptedException {
        Logger logger = Logger.getLogger(RemovalListenerImpl.class.getName());
        RemovalListener<Integer, Entity> listener;

        listener = new RemovalListener<Integer, Entity>() {
            @Override
            public void onRemoval(RemovalNotification<Integer, Entity> notification) {
                if (notification.wasEvicted()) {
                    String event = String.format("Removed entry: %s : %s; cause: %s ", notification.getKey(), notification.getValue(), notification.getCause());
                    logger.log(Level.INFO, event);
                }
            }
        };

        //given
        GuavaCache cache = new GuavaCache(listener);
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
        Thread.sleep(15000L);
        //then
        System.out.println(cache.size());
        assertEquals(1, cache.size());
        System.out.println(cache.asMap());
    }
}