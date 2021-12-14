package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.junit.Assert.*;

public class CourtTypeTest {

    @Test
    public void valueOfCourtType_success() {
        assertEquals(CourtType.getFrom("CROWN COURT"), CourtType.CROWN_COURT);
    }

    @Test
    public void valueOfCourtType_nullParamenter_ReturnsNull() {
        assertNull(CourtType.getFrom(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfCourtType_valueNotFound_throwsException() {
        assertNull(CourtType.getFrom("THROWS_EXCEPTION"));
    }

}