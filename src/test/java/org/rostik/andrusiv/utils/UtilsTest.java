package org.rostik.andrusiv.utils;

import junit.framework.TestCase;

import java.util.Arrays;


public class UtilsTest extends TestCase {

    public void testMergeSort() {
        int[] array = new int[]{1, 8, 12, 4, 7, 3, 28, 13, 25, 20};
        int[] expected = new int[]{1, 3, 4, 7, 8, 12, 13, 20, 25, 28};

        Utils.mergeSort(array);

        assertEquals(Arrays.toString(expected), Arrays.toString(array));
    }
}