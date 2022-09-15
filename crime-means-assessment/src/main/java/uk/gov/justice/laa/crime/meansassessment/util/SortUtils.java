package uk.gov.justice.laa.crime.meansassessment.util;

import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentDTO;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class SortUtils {

    public static <T, U extends Comparable> void sortListWithComparing(List<T> t, Function<T, U> compFunction, Function<T, U> thenCompFunc, Comparator<U> comparator) {
        t.sort(Comparator.comparing(compFunction, comparator).thenComparing(thenCompFunc, comparator));
    }

    public static <U extends Comparable> Comparator<U> getComparator() {
        return Comparator.naturalOrder();
    }

    public static <U extends Comparable> Comparator<U> getReverseComparator() {
        return Comparator.reverseOrder();
    }
}
