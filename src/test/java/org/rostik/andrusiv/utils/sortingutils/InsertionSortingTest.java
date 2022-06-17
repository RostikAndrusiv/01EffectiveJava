package org.rostik.andrusiv.utils.sortingutils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.rostik.andrusiv.TestUtil.TestDataUtil.getArray;

public class InsertionSortingTest {
    @Test
    public void testInsertionSort(){
        int[] array = getArray(100);

        int[] expected = Arrays.copyOf(array, array.length);
        Arrays.sort(expected);
        Sorting sortUtil = new InsertionSorting();
        sortUtil.sort(array);

        assertEquals(Arrays.toString(expected), Arrays.toString(array));
    }
}