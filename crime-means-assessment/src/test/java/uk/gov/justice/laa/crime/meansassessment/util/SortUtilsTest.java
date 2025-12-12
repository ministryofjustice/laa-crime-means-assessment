package uk.gov.justice.laa.crime.meansassessment.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.jupiter.api.Test;

class SortUtilsTest {

    @Test
    void testSortUtilConstructorIsPrivate() throws NoSuchMethodException {
        assertThat(SortUtils.class.getDeclaredConstructors()).hasSize(1);
        Constructor<SortUtils> constructor = SortUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }

    @Test
    void testSort_whenNullIsPassed_NullIsReturned() {
        List<String> list = null;
        SortUtils.sortListWithComparing(list, null, null, null);
        assertThat(list).isNull();
    }
}
