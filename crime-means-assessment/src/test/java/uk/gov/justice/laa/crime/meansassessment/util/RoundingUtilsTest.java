package uk.gov.justice.laa.crime.meansassessment.util;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RoundingUtilsTest {
    @Test
    public void testRoundingUtilConstructorIsPrivate() throws NoSuchMethodException {
        assertThat(RoundingUtils.class.getDeclaredConstructors()).hasSize(1);
        Constructor<RoundingUtils> constructor = RoundingUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
}
