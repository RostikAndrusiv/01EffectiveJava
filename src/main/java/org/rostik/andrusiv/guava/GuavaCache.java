package org.rostik.andrusiv.guava;

import com.google.common.cache.*;
import org.rostik.andrusiv.model.Entity;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO incapsulate, onRemoval in constructor
public class GuavaCache {
    static Logger logger = Logger.getLogger(GuavaCache.class.getName());

    private GuavaCache() {
        //not called
    }

    public static final Cache<Integer, Entity> cache = CacheBuilder.newBuilder()
            .initialCapacity(2)
            .maximumSize(5)
            .recordStats()
            .removalListener(GuavaCache::onRemoval)
            .expireAfterAccess(50, TimeUnit.MILLISECONDS)
            .build();


    private static void onRemoval(RemovalNotification<Integer, Entity> notification) {
        if (notification.wasEvicted()) {
            String event = String.format("Removed entry: %s : %s; cause: %s ", notification.getKey(), notification.getValue(), notification.getCause());
            logger.log(Level.INFO, event);
        }
    }

}
