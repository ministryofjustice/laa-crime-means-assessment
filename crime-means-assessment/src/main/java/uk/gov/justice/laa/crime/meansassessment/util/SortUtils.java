package uk.gov.justice.laa.crime.meansassessment.util;

import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public final class SortUtils {

    public static <T, U extends Comparable<? super U>, V extends Comparable<? super V>> void sortListWithComparing(
            List<T> t,
            Function<T, U> compFunction,
            Comparator<U> primaryComparator,
            Function<T, V> thenCompFunc,
            Comparator<V> secondComparator) {
        if (t != null) {
            t.sort(Comparator.comparing(compFunction, primaryComparator).thenComparing(thenCompFunc, secondComparator));
        }
    }

    public static <U extends Comparable<? super U>> Comparator<U> getComparator() {
        return Comparator.naturalOrder();
    }

    public static <U extends Comparable<? super U>> Comparator<U> getReverseComparator() {
        return Comparator.reverseOrder();
    }
}
