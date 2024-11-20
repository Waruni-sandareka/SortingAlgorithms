package org.uoc.group.groupassignment;

import java.util.*;

public class SortingAlgorithms {

    // Insertion Sort
    public static List<Integer> insertionSort(List<Integer> list) {
        List<Integer> sortedList = new ArrayList<>(list);
        for (int i = 1; i < sortedList.size(); i++) {
            int key = sortedList.get(i);
            int j = i - 1;
            while (j >= 0 && sortedList.get(j) > key) {
                sortedList.set(j + 1, sortedList.get(j));
                j = j - 1;
            }
            sortedList.set(j + 1, key);
        }
        return sortedList;
    }

    // Shell Sort
    public static List<Integer> shellSort(List<Integer> list) {
        List<Integer> sortedList = new ArrayList<>(list);
        int n = sortedList.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = sortedList.get(i);
                int j;
                for (j = i; j >= gap && sortedList.get(j - gap) > temp; j -= gap) {
                    sortedList.set(j, sortedList.get(j - gap));
                }
                sortedList.set(j, temp);
            }
        }
        return sortedList;
    }

    // Merge Sort
    public static List<Integer> mergeSort(List<Integer> list) {
        if (list.size() <= 1) return list;
        int mid = list.size() / 2;
        List<Integer> left = mergeSort(list.subList(0, mid));
        List<Integer> right = mergeSort(list.subList(mid, list.size()));
        return merge(left, right);
    }

    private static List<Integer> merge(List<Integer> left, List<Integer> right) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i) < right.get(j)) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        result.addAll(left.subList(i, left.size()));
        result.addAll(right.subList(j, right.size()));
        return result;
    }

    // Quick Sort
    public static List<Integer> quickSort(List<Integer> list) {
        if (list.size() <= 1) return list;
        int pivot = list.get(list.size() / 2);
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        for (int num : list) {
            if (num < pivot) left.add(num);
            else if (num > pivot) right.add(num);
        }
        List<Integer> result = new ArrayList<>();
        result.addAll(quickSort(left));
        result.add(pivot);
        result.addAll(quickSort(right));
        return result;
    }

    // Heap Sort
    public static List<Integer> heapSort(List<Integer> list) {
        List<Integer> heap = new ArrayList<>(list);
        int n = heap.size();

        // Build heap (rearrange list)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(heap, n, i);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            Collections.swap(heap, 0, i);
            heapify(heap, i, 0);
        }

        return heap;
    }

    private static void heapify(List<Integer> heap, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && heap.get(left) > heap.get(largest)) largest = left;
        if (right < n && heap.get(right) > heap.get(largest)) largest = right;

        if (largest != i) {
            Collections.swap(heap, i, largest);
            heapify(heap, n, largest);
        }
    }
}
