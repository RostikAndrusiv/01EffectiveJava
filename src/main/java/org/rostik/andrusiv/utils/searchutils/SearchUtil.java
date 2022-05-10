package org.rostik.andrusiv.utils.searchutils;

import org.rostik.andrusiv.utils.sortingutils.SortingInterface;

public class SearchUtil {
    public int binarySearchRecursive(int[] arr, int left, int right, int numberToSearch) {
        if (right >= left) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == numberToSearch)
                return mid;
            if (arr[mid] > numberToSearch)
                return binarySearchRecursive(arr, left, mid - 1, numberToSearch);
            return binarySearchRecursive(arr, mid + 1, right, numberToSearch);
        }
        return -1;
    }

    public int binarySearchIterative(int[] array, int valueToSearch) {
        int left = 0;
        int right = array.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (array[mid] == valueToSearch)
                return mid;
            if (array[mid] < valueToSearch)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return -1;
    }

    public int binarySearchIterativeNotSorted(int[] array, int valueToSearch, SortingInterface sortingUtil) {
        sortingUtil.sort(array);
        int left = 0;
        int right = array.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (array[mid] == valueToSearch)
                return mid;
            if (array[mid] < valueToSearch)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return -1;
    }

}
