package org.rostik.andrusiv.TestUtil;

import java.util.concurrent.ThreadLocalRandom;

public class TestDataUtil {

    public static int[] getArray(int elements){
        int[] array = new int[elements];
        for (int i = 0; i < array.length; i++) {
            array[i] = ThreadLocalRandom.current().nextInt(1, 100000 + 1);
        }
        return array;
    }
}
