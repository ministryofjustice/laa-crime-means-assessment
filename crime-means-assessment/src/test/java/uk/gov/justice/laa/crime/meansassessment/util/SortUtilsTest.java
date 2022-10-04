package uk.gov.justice.laa.crime.meansassessment.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.*;

public class SortUtilsTest {
    @Test
    public void testSortUtilConstructorIsPrivate() throws NoSuchMethodException {
        assertEquals("There must be only one constructor", 1,
                SortUtils.class.getDeclaredConstructors().length);
        Constructor<SortUtils> constructor = SortUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    @Test
    public void testSort_whenNullIsPassed_NullIsReturned() {
        List<String> list = null;
        SortUtils.sortListWithComparing(list, null, null, null);
        assertNull(list);
    }
}
