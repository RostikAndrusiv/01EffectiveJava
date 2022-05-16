package org.rostik.andrusiv.guava;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.rostik.andrusiv.model.Entity;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RemovalListenerImpl implements RemovalListener<Integer, Entity> {

    Logger logger = Logger.getLogger(RemovalListenerImpl.class.getName());

    public RemovalListenerImpl() {
    }

    @Override
    public void onRemoval(RemovalNotification<Integer, Entity> notification) {
        if (notification.wasEvicted()) {
            String event = String.format("Removed entry: %s : %s; cause: %s ", notification.getKey(), notification.getValue(), notification.getCause());
            logger.log(Level.INFO, event);
        }
    }
}
