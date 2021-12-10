package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import org.junit.Test;

import static org.junit.Assert.*;

public class AssessmentStatusTest {

    @Test
    public void valueOfAssessmentStatus_success() {
        assertEquals(AssessmentStatus.getFrom("IN PROGRESS"), AssessmentStatus.IN_PROGRESS);
    }

    @Test
    public void valueOfAssessmentStatus_nullParamenter_ReturnsNull() {
        assertNull(AssessmentStatus.getFrom(null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void valueOfAssessmentStatus_valueNotFound_ReturnsNull() {
        assertNull(AssessmentStatus.getFrom("THROWS_EXCEPTION"));
    }
}