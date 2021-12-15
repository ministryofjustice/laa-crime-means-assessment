package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.junit.Assert.*;

public class CurrentStatusTest {

    @Test
    public void valueOfCurrentStatusFromString_success() {
        assertEquals(CurrentStatus.getFrom("IN PROGRESS"), CurrentStatus.IN_PROGRESS);
    }

    @Test
    public void valueOfCurrentStatusFromString_nullParamenter_ReturnsNull() {
        assertNull(CurrentStatus.getFrom(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfCurrentStatusFromString_valueNotFound_throwsException() {
        CurrentStatus.getFrom("THROWS_EXCEPTION");
    }
}