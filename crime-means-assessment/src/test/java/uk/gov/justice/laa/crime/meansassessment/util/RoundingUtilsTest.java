package uk.gov.justice.laa.crime.meansassessment.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RoundingUtilsTest {
    @Test
    public void testRoundingUtilConstructorIsPrivate() throws NoSuchMethodException {
        assertEquals("There must be only one constructor", 1,
                RoundingUtils.class.getDeclaredConstructors().length);
        Constructor<RoundingUtils> constructor = RoundingUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
}
