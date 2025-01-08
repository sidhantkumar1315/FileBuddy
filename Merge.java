/**
 * This class performs all the main functions (merging, common items and different items)
 * */
public class Merge {

    // the following method returns merge list by merging two orderedlists using Two Finger Algorithm
    public static <T extends Comparable<T>> OrderedList<T> merge(OrderedList<T> list1, OrderedList<T> list2) {
        OrderedList<T> result = new OrderedList<>();
        int f1 = 0, f2 = 0;

        while (f1 < list1.size() && f2 < list2.size()) {
            if (list1.get(f1).compareTo(list2.get(f2)) < 0) {
                result.add(list1.get(f1));
                f1++;
            } else if (list2.get(f2).compareTo(list1.get(f1)) < 0) {
                result.add(list2.get(f2));
                f2++;
            } else {
                result.add(list1.get(f1));
                f1++;
                f2++;
            }
        }
        if (f1 == list1.size()) {
            for (int i = f2; i < list2.size(); i++) {
                result.add(list2.get(i));
            }
        }
        if (f2 == list2.size()) {
            for (int i = f1; i < list1.size(); i++) {
                result.add(list1.get(i));
            }
        }
        return result;
    }

    // the following method returns a list of common elements of both 2 files (Modified Two Finger Algorithm)
    public static <T extends Comparable<T>> OrderedList<T> common(OrderedList<T> list1, OrderedList<T> list2) {
        OrderedList<T> result = new OrderedList<>();
        int f1 = 0, f2 = 0;

        while (f1 < list1.size() && f2 < list2.size()) {
            if (list1.get(f1).compareTo(list2.get(f2)) < 0) {
                f1++;
            } else if (list2.get(f2).compareTo(list1.get(f1)) < 0) {
                f2++;
            } else {
                result.add(list1.get(f1));
                f1++;
                f2++;
            }
        }
        return result;
    }

    // the following method returns a list of different elements of both 2 files (Modified Two Finger Algorithm)
    public static <T extends Comparable<T>> OrderedList<T> difference(OrderedList<T> list1, OrderedList<T> list2) {
        OrderedList<T> result = new OrderedList<>();
        int f1 = 0, f2 = 0;

        while (f1 < list1.size() && f2 < list2.size()) {
            if (list1.get(f1).compareTo(list2.get(f2)) < 0) {
                result.add(list1.get(f1));
                f1++;
            } else if (list2.get(f2).compareTo(list1.get(f1)) < 0) {
                f2++;
            } else {
                f1++;
                f2++;
            }
        }
        while (f1 < list1.size()) {
            result.add(list1.get(f1));
            f1++;
        }
        return result;
    }
}