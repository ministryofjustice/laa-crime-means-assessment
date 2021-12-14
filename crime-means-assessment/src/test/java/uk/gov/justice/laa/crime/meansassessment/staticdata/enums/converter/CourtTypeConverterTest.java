package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.Before;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CourtType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CourtTypeConverterTest {

    private CourtTypeConverter courtTypeConverter;

    @Before
    public void init(){
        courtTypeConverter = new CourtTypeConverter();
    }

    @Test
    public void convertToDatabaseColumn_success() {
        var dbValueReturned = courtTypeConverter.convertToDatabaseColumn(CourtType.CROWN_COURT);
        assertEquals(CourtType.CROWN_COURT.getCourtType(), dbValueReturned);
    }
    @Test
    public void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = courtTypeConverter.convertToDatabaseColumn(null);
        assertNull(nullValueReturned);
    }

    @Test
    public void convertToCourtType_success(){
        var courtTypeReturned = courtTypeConverter.convertToEntityAttribute("CROWN COURT");
        assertEquals(CourtType.CROWN_COURT, courtTypeReturned);
    }
    @Test
    public void convertToCourtType_nullValueReturned(){
        var nullValueReturned = courtTypeConverter.convertToEntityAttribute(null);
        assertNull(nullValueReturned);
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertToCourtType_valueNotFound_throwsException() {
        courtTypeConverter.convertToEntityAttribute("THROWS_EXCEPTION");
    }
}