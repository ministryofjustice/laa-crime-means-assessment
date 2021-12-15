package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.Before;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CurrentStatusConverterTest {

    private CurrentStatusConverter currentStatusConverter;

    @Before
    public void init(){
        currentStatusConverter = new CurrentStatusConverter();
    }

    @Test
    public void convertToDatabaseColumn_success() {
        var dbValueReturned = currentStatusConverter.convertToDatabaseColumn(CurrentStatus.IN_PROGRESS);
        assertEquals(CurrentStatus.IN_PROGRESS.getStatus(), dbValueReturned);
    }
    @Test
    public void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = currentStatusConverter.convertToDatabaseColumn(null);
        assertNull(nullValueReturned);
    }

    @Test
    public void convertToCurrentStatus_success(){
        var currentStatusReturned = currentStatusConverter.convertToEntityAttribute("IN PROGRESS");
        assertEquals(CurrentStatus.IN_PROGRESS, currentStatusReturned);
    }
    @Test
    public void convertToCurrentStatus_nullValueReturned(){
        var nullValueReturned = currentStatusConverter.convertToEntityAttribute(null);
        assertNull(nullValueReturned);
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertToCurrentStatus_valueNotFound_throwsException() {
        currentStatusConverter.convertToEntityAttribute("THROWS_EXCEPTION");
    }
}