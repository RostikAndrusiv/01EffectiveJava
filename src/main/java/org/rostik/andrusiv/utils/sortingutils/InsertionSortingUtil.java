package org.rostik.andrusiv.utils.sortingutils;

public class InsertionSortingUtil implements Sortable {
    @Override
    public int[] sort(int[] arrayToSort) {
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
        return arrayToSort;
    }
}
