package org.rostik.andrusiv.lfu;

import org.rostik.andrusiv.model.Entity;

public interface LfuCache {

    void put(int id, Entity entity);

    Entity get(int id);

    void printStats();

    int size();
}
