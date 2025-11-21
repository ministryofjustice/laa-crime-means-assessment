package uk.gov.justice.laa.crime.meansassessment.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class SortUtilsTest {

    @Test
    void testSortUtilConstructorIsPrivate() throws NoSuchMethodException {
        Constructor<SortUtils> constructor = SortUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(UnsupportedOperationException.class)
                .hasRootCauseMessage("This is a utility class and cannot be instantiated");
    }

    @Test
    void sortList_withNullList_doesNothing() {
        List<String> list = null;
        SortUtils.sortListWithComparing(list, null, null, null, null);
        assertThat(list).isNull();
    }

    @Test
    void sortList_sortsByPrimaryThenSecondary() {
        List<Foo> list = new ArrayList<>(List.of(
                new Foo("b", 2),
                new Foo("a", 3),
                new Foo("a", 1)
        ));

        SortUtils.sortListWithComparing(
                list,
                Foo::a, SortUtils.getComparator(),
                Foo::b, SortUtils.getComparator()
        );

        assertThat(list)
                .asInstanceOf(InstanceOfAssertFactories.list(Foo.class))
                .extracting(Foo::a, Foo::b)
                .containsExactly(
                        tuple("a", 1),
                        tuple("a", 3),
                        tuple("b", 2)
                );
    }

    @Test
    void sortList_canUseReverseOrder() {
        List<Foo> list = new ArrayList<>(List.of(
                new Foo("a", 1),
                new Foo("c", 3),
                new Foo("b", 2)
        ));

        SortUtils.sortListWithComparing(
                list,
                Foo::a, SortUtils.getReverseComparator(),
                Foo::b, SortUtils.getReverseComparator()
        );

        assertThat(list)
                .asInstanceOf(InstanceOfAssertFactories.list(Foo.class))
                .extracting(Foo::a)
                .containsExactly("c", "b", "a");
    }

    record Foo(String a, Integer b) {}
}
