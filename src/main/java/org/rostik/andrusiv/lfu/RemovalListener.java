package org.rostik.andrusiv.lfu;

public interface RemovalListener {

    void log(Object o, RemovalCauseEnum removalCauseEnum);
}
