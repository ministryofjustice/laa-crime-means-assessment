package uk.gov.justice.laa.crime.meansassessment.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SortUtilsTest {
    @Test
    public void testSortUtilConstructorIsPrivate() throws NoSuchMethodException {
        assertEquals("There must be only one constructor", 1,
                SortUtils.class.getDeclaredConstructors().length);
        Constructor<SortUtils> constructor = SortUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
}
