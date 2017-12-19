package util;

import java.util.ArrayList;
import java.util.List;

public class Lists {

    public static <T> List<List<T>> splitIncludingDelimiterAsFirstElement(List<T> target, GenericPredicate<T> predicate) {
        List<List<T>> splitLists = new ArrayList<>();

        List<T> currentList = new ArrayList<>();

        for (T i : target) {
            if (predicate.test(i)) {
                if (!currentList.isEmpty()) {
                    splitLists.add(currentList);
                }
                currentList = new ArrayList<>();
            }
            currentList.add(i);
        }
        if (!currentList.isEmpty()) {
            splitLists.add(currentList);
        }
        return splitLists;
    }

    public interface GenericPredicate<T> {
        boolean test(T t);
    }
}
