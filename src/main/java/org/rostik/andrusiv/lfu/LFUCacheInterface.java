package org.rostik.andrusiv.lfu;

import org.rostik.andrusiv.model.Entity;

public interface LFUCacheInterface {

    void put(int id, Entity entity);

    Entity get(int id);

    int getNumberOfEvictions();

    String getStatistic();

    int size();
}
