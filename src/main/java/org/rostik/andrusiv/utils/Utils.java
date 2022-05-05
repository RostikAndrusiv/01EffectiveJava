package org.rostik.andrusiv.utils;

public class Utils {

    //Added for sonarlint
    private Utils() {
        //not called
    }

    // Extra mile 1.1
    public static int binarySearchIterative(int[] valueToSearch, int x)
        {
            int left = 0;
            int right = valueToSearch.length - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (valueToSearch[mid] == x)
                    return mid;
                if (valueToSearch[mid] < x)
                    left = mid + 1;
                else
                    right = mid - 1;
            }
            return -1;
        }

    // Extra mile 1.2
    public static int binarySearchRecursive(int[] arr, int left, int right, int numberToSearch) {
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

    // Extra mile 2
    public static void mergeSort(int[] arrayToSort) {
        if (arrayToSort.length < 2) {
            return;
        }

        int mid = arrayToSort.length / 2;
        int[] l = new int[mid];
        int[] r = new int[arrayToSort.length - mid];

        System.arraycopy(arrayToSort, 0, l, 0, mid);

        if (arrayToSort.length - mid >= 0) System.arraycopy(arrayToSort, mid, r, 0, arrayToSort.length - mid);

        mergeSort(l);
        mergeSort(r);

        int i = 0;
        int j = 0;
        int k = 0;

        while (i < l.length && j < r.length) {
            if (l[i] < r[j]) {
                arrayToSort[k++] = l[i++];
            } else {
                arrayToSort[k++] = r[j++];
            }
        }
        while (i < l.length) {
            arrayToSort[k++] = l[i++];
        }
        while (j < r.length) {
            arrayToSort[k++] = r[j++];
        }
    }

    // Extra mile 3
    public static void insertionSort(int[] arrayToSort)
    {
        int n = arrayToSort.length;
        for (int i = 1; i < n; ++i) {
            int pointer = arrayToSort[i];
            int j = i - 1;

            while (j >= 0 && arrayToSort[j] > pointer) {
                arrayToSort[j + 1] = arrayToSort[j];
                j = j - 1;
            }
            arrayToSort[j + 1] = pointer;
        }
    }

    // Extra mile 4


}
