package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.Before;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AssessmentStatusConverterTest {

    private AssessmentStatusConverter assessmentStatusConverter;

    @Before
    public void init(){
        assessmentStatusConverter = new AssessmentStatusConverter();
    }

    @Test
    public void convertToDatabaseColumn_success() {
        var dbValueReturned = assessmentStatusConverter.convertToDatabaseColumn(AssessmentStatus.IN_PROGRESS);
        assertEquals(AssessmentStatus.IN_PROGRESS.getStatus(), dbValueReturned);
    }
    @Test
    public void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = assessmentStatusConverter.convertToDatabaseColumn(null);
        assertNull(nullValueReturned);
    }

    @Test
    public void convertToAssessmentStatus_success(){
        var assessmentStatusReturned = assessmentStatusConverter.convertToEntityAttribute("IN PROGRESS");
        assertEquals(AssessmentStatus.IN_PROGRESS, assessmentStatusReturned);
    }
    @Test
    public void convertToAssessmentStatus_nullValueReturned(){
        var nullValueReturned = assessmentStatusConverter.convertToEntityAttribute(null);
        assertNull(nullValueReturned);
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertToAssessmentStatus_valueNotFound_throwsException() {
        assessmentStatusConverter.convertToEntityAttribute("THROWS_EXCEPTION");
    }

}