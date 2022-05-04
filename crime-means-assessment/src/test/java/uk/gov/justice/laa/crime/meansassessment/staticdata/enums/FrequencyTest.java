package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FrequencyTest {

    @Test
    public void valueOfFrequencyFromString_success() {
        assertEquals(Frequency.TWO_WEEKLY, Frequency.getFrom("2WEEKLY"));
    }

    @Test
    public void valueOfFrequencyFromString_nullParamenter_ReturnsNull() {
        assertNull(Frequency.getFrom(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfFrequencyFromString_valueNotFound_throwsException() {
        Frequency.getFrom("THROWS_EXCEPTION");
    }
}