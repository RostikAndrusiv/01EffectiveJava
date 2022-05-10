package org.rostik.andrusiv.utils.sortingutils;

public class MergeSortingUtil implements SortingInterface{
    @Override
    public void sort(int[] arrayToSort) {
        if (arrayToSort.length < 2) {
            return;
        }

        int mid = arrayToSort.length / 2;
        int[] l = new int[mid];
        int[] r = new int[arrayToSort.length - mid];

        System.arraycopy(arrayToSort, 0, l, 0, mid);

        if (arrayToSort.length - mid >= 0) System.arraycopy(arrayToSort, mid, r, 0, arrayToSort.length - mid);

        sort(l);
        sort(r);

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
