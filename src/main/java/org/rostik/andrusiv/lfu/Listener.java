package org.rostik.andrusiv.lfu;

import java.util.Map;

public interface Listener {

    void log(Map.Entry<Integer, ?> entry, Cause cause);
}
