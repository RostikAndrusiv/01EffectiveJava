package org.rostik.andrusiv.lfu;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RemovalListenerImpl implements RemovalListener {

    Logger logger = Logger.getLogger(RemovalListenerImpl.class.getName());

    @Override
    public void log(Object o, RemovalCauseEnum removalCause) {
        String event = String.format("Removing item: %s, CAUSE: %s", o, removalCause);
        logger.log(Level.INFO, event);
    }
}
