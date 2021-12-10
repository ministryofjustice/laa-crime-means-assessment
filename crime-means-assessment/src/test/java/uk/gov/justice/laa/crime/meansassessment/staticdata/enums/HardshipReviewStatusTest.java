package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.junit.Assert.*;

public class HardshipReviewStatusTest {

    @Test
    public void valueOfHardshipReviewStatus_success() {
        assertEquals(HardshipReviewStatus.getFrom("IN PROGRESS"), HardshipReviewStatus.IN_PROGRESS);
    }

    @Test
    public void valueOfHardshipReviewStatus_nullParamenter_ReturnsNull() {
        assertNull(HardshipReviewStatus.getFrom(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfHardshipReviewStatus_valueNotFound_ReturnsNull() {
        assertNull(HardshipReviewStatus.getFrom("THROWS_EXCEPTION"));
    }
}