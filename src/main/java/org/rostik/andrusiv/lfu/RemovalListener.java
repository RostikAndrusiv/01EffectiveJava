package org.rostik.andrusiv.lfu;

public interface RemovalListener {

    void onRemove(Object o, RemovalCauseEnum removalCauseEnum);
}
