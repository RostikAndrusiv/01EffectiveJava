package org.rostik.andrusiv.lfu;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO rename class
@Slf4j
public class LogOnRemoval implements RemovalListener {
    private static Logger logger = LoggerFactory.getLogger(LogOnRemoval.class.getName());
    @Override
    public void onRemove(Object o, RemovalCauseEnum removalCause) {
        String event = String.format("Removing item: %s, CAUSE: %s", o, removalCause);
        logger.info(event);
    }
}
