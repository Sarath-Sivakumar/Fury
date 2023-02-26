package app.personal.Utls;

import java.util.ArrayList;

import app.personal.MVVM.Entity.debtEntity;

public class ListSortUtil {

    private final ArrayList<debtEntity> debtList = new ArrayList<>();
    private int partition(ArrayList<debtEntity> arr, int low, int high) {
        int pivot = arr.get(high).getAmount();
        int i = (low - 1); // index of smaller element
        for (int j = low; j < high; j++) {
            // If current element is smaller than or
            // equal to pivot
            if (arr.get(j).getAmount() <= pivot) {
                i++;

                // swap arr[i] and arr[j]
                debtEntity temp = arr.get(i);
                arr.add(i, arr.get(j));
                arr.add(j, temp);
            }
        }

        // swap arr[i+1] and arr[high] (or pivot)
        debtEntity temp = arr.get(i + 1);
        arr.add((i + 1), arr.get(high));
        arr.add(high, temp);

        return i + 1;
    }

//      arr[] --> Array to be sorted,
//      low  --> Starting index,
//      high  --> Ending index
    private void sort(ArrayList<debtEntity> arr, int low, int high) {
        if (low < high) {
            /* pi is partitioning index, arr[pi] is
              now at right place */
            int pi = partition(arr, low, high);

            // Recursively sort elements before
            // partition and after partition
            sort(arr, low, pi - 1);
            sort(arr, pi + 1, high);
        }
    }

    /* A utility function to print array of size n */
    public debtEntity getLowestItem(ArrayList<debtEntity> entity) {
        return entity.get(0);
    }

    public debtEntity getHighestItem(ArrayList<debtEntity> entity) {
        return entity.get(entity.size()-1);
    }

    public ArrayList<debtEntity> getSortedList(){
        return debtList;
    }

    // Driver program
    public ListSortUtil(ArrayList<debtEntity> arr) {
        debtList.addAll(arr);
        sort(debtList, 0, arr.size() - 1);
    }

}
