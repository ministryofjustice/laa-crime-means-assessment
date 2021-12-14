package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssessmentStatusTest {

    @Test
    public void valueOfAssessmentStatusFromString_success() {
        assertEquals(AssessmentStatus.getFrom("IN PROGRESS"), AssessmentStatus.IN_PROGRESS);
    }

    @Test
    public void valueOfAssessmentStatusFromString_nullParamenter_ReturnsNull() {
        assertNull(AssessmentStatus.getFrom(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfAssessmentStatusFromString_valueNotFound_throwsException() {
        AssessmentStatus.getFrom("THROWS_EXCEPTION");
    }
}