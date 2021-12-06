package uk.gov.justice.laa.crime.meansassessment.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FrequencyTest {

    @Test
    public void valueOfFrequency_success() {
        assertEquals(Frequency.getFrom("2WEEKLY"), Frequency.TWO_WEEKLY);
    }

    @Test
    public void valueOfFrequency_nullParamenter_ReturnsNull() {
        assertNull(Frequency.getFrom(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfFrequency_valueNotFound_ReturnsNull() {
        assertNull(Frequency.getFrom("THROWS_EXCEPTION"));
    }
}