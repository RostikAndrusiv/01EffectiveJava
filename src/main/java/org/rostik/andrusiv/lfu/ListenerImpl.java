package org.rostik.andrusiv.lfu;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListenerImpl implements Listener{

    Logger logger;


    @Override
    public void log(Map.Entry<Integer, ?> entry, Cause cause) {
        String event = String.format("Removing item: %s, CAUSE: %s", entry, cause);
        logger.log(Level.INFO, event);
    }
}
