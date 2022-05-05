package org.rostik.andrusiv.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testMergeSort() {
        int[] array = getArray(100);

        int[] expected = Arrays.copyOf(array, array.length);
        Arrays.sort(expected);

        Utils.mergeSort(array);

        assertEquals(Arrays.toString(expected), Arrays.toString(array));
    }

    @Test
    public void testBinarySearchIterative(){
        int[] array = getArray(10000);
        Arrays.sort(array);
        int valueToFind = array[8978];
        int index = Utils.binarySearchIterative(array, valueToFind);
        long start = System.nanoTime();
        int indexNotExist = Utils.binarySearchIterative(array, 200000);
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
        int index = Utils.binarySearchRecursive(array, 0, array.length-1, valueToFind);
        long start = System.nanoTime();
        int indexNotExist = Utils.binarySearchRecursive(array, 0, array.length-1, 200000);
        long end = System.nanoTime();
        long timeToExecute = end - start;
        assertEquals(8978, index);
        assertEquals(-1, indexNotExist);

        System.out.println("TimeToExecute: " + timeToExecute);
    }

    @Test
    public void testInsertionSort(){
        int[] array = getArray(100);

        int[] expected = Arrays.copyOf(array, array.length);
        Arrays.sort(expected);

        Utils.insertionSort(array);

        assertEquals(Arrays.toString(expected), Arrays.toString(array));
    }

    private int[] getArray(int elements){
        int[] array = new int[elements];
        for (int i = 0; i < array.length; i++) {
            array[i] = ThreadLocalRandom.current().nextInt(1, 100000 + 1);
        }
        return array;
    }

}