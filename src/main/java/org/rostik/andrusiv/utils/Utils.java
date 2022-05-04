package org.rostik.andrusiv.utils;

public class Utils {

    //Added for sonarlint
    private Utils() {
        //not called
    }

    public static int binarySearchIterative(int[] arr, int x)
        {
            int l = 0, r = arr.length - 1;
            while (l <= r) {
                int m = l + (r - l) / 2;
                if (arr[m] == x)
                    return m;
                if (arr[m] < x)
                    l = m + 1;
                else
                    r = m - 1;
            }
            return -1;
        }

    public static int binarySearchRecursive(int[] arr, int left, int right, int number) {
        if (right >= left) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == number)
                return mid;
            if (arr[mid] > number)
                return binarySearchRecursive(arr, left, mid - 1, number);
            return binarySearchRecursive(arr, mid + 1, right, number);
        }
        return -1;
    }

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
}
