package org.rostik.andrusiv.lfu;

import org.junit.Test;
import org.rostik.andrusiv.model.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class LfuCacheImplTest {
    private static final Logger logger = LoggerFactory.getLogger(LfuCacheImplTest.class.getName());
    //TODO add assertions
    @Test
    public void testCacheNotTimeBased() {
        RemovalListener removalListener = (o, removalCauseEnum) -> {
            String event = String.format("Removing item: %s, CAUSE: %s", o, removalCauseEnum);
            logger.info(event);
        };
        //given
        LfuCacheImpl cache = LfuCacheImpl.builder()
                .capacity(2)
                .removalListener(removalListener)
                .build();
        //when
        cache.put(1, new Entity("1"));
        //then
        assertEquals(1, cache.size());
        //when
        cache.put(2, new Entity("2"));
        //then
        assertEquals(2, cache.size());
        //when
        cache.get(1);
        cache.get(1);
        cache.put(3, new Entity("3"));
        //then
        assertEquals(2, cache.size());
        assertFalse(cache.containsKey(2));
        assertTrue(cache.containsKey(1));
        assertTrue(cache.containsKey(3));
        //when
        cache.put(3, new Entity("three"));
        //then
        assertEquals(2, cache.size());
        assertEquals(new Entity("three"), cache.get(3));
        assertNull(cache.get(256));
    }

    @Test
    public void testCacheTimeBased() throws InterruptedException {
        RemovalListener removalListener = new LogOnRemoval();
        //given
        LfuCacheImpl cache = LfuCacheImpl.builder()
                .capacity(2)
                .isTimeBased(true)
                .expiryInMillis(1000)
                .removalListener(removalListener)
                .build();
        //when
        cache.put(1, new Entity("1"));
        //then
        assertEquals(new Entity("1"), cache.get(1));
        assertEquals(1, cache.size());

        //when
        Thread.sleep(1000L);
        //then
        assertNull(cache.get(1));
        assertEquals(0, cache.size());
        //when
        cache.put(1, new Entity("5"));
        //then
        assertEquals(new Entity("5"), cache.get(1));
        assertEquals(1, cache.size());
        //when
        Thread.sleep(1000L);
        //then
        assertNull(cache.get(1));
        assertEquals(0, cache.size());
    }

}