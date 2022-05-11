package org.rostik.andrusiv.utils.searchutils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.rostik.andrusiv.utils.sortingutils.InsertionSortingUtil;
import org.rostik.andrusiv.utils.sortingutils.MergeSortingUtil;
import org.rostik.andrusiv.utils.sortingutils.SortingInterface;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.rostik.andrusiv.TestUtil.TestDataUtil.getArray;

@RunWith(Parameterized.class)
public class SearchUtilTest {

    SortingInterface sortingUtil;

    SearchUtil searchUtil = new SearchUtil();

    public SearchUtilTest(SortingInterface sortingUtil){
        this.sortingUtil = sortingUtil;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
                { new InsertionSortingUtil() },
                { new MergeSortingUtil()}
        });
    }

    @Test
    public void testBinarySearchIterativeNotSorted(){
        int[] array = getArray(10000);
        array[array.length-1] = 100000;
        int valueToFind = 100000;
        int index = searchUtil.binarySearchIterativeNotSorted(array, valueToFind, sortingUtil);
        long start = System.nanoTime();
        int indexNotExist = searchUtil.binarySearchIterativeNotSorted(array, Integer.MAX_VALUE, sortingUtil);
        long end = System.nanoTime();
        long timeToExecute = end - start;
        assertEquals(array.length-1, index);
        assertEquals(-1, indexNotExist);

        System.out.println("TimeToExecute: " + timeToExecute);
    }

    @Test
    public void testBinarySearchIterative(){
        int[] array = getArray(10000);
        Arrays.sort(array);
        int valueToFind = array[8978];
        int index = searchUtil.binarySearchIterative(array, valueToFind);
        long start = System.nanoTime();
        int indexNotExist = searchUtil.binarySearchIterative(array, 200000);
        long end = System.nanoTime();
        long timeToExecute = end - start;
        assertEquals(8978, index);
        assertEquals(-1, indexNotExist);

        System.out.println("TimeToExecute: " + timeToExecute);
    }

    @Test
    public void testBinarySearchRecursive(){
        int[] array = getArray(10000);
        Arrays.sort(array);
        int valueToFind = array[8978];
        int index = searchUtil.binarySearchRecursive(array, 0, array.length-1, valueToFind);
        long start = System.nanoTime();
        int indexNotExist = searchUtil.binarySearchRecursive(array, 0, array.length-1, 200000);
        long end = System.nanoTime();
        long timeToExecute = end - start;
        assertEquals(8978, index);
        assertEquals(-1, indexNotExist);

        System.out.println("TimeToExecute: " + timeToExecute);
    }

}