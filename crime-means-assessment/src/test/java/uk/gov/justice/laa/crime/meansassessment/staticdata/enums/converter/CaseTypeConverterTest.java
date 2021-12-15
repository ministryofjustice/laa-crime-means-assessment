package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.Before;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CaseTypeConverterTest {

    private static final String VALID_VALUE = "APPEAL_CC";
    private static final String INVALID_VALUE = "INVALID_VALUE";

    private CaseTypeConverter caseTypeConverter = new CaseTypeConverter();

    @Test
    public void givenWhenEnumIsProvidedThenMatchingValueForDBIsReturned() {
        String result = caseTypeConverter.convertToDatabaseColumn(CaseType.APPEAL_CC);
        assertEquals(CaseType.APPEAL_CC.getCaseType(), result);
    }

    @Test
    public void givenWhenValidValueIsInDBThenCorrectEnumIsReturned (){
        CaseType result = caseTypeConverter.convertToEntityAttribute(VALID_VALUE);
        assertEquals(CaseType.APPEAL_CC, result);
    }
    @Test
    public void givenWhenEnumIsNullThenNullDBValueReturned() {
        String result = caseTypeConverter.convertToDatabaseColumn(null);
        assertNull(result);
    }

    @Test
    public void givenWhenDBValueIsNullThenNoEnumIsReturned(){
        CaseType result = caseTypeConverter.convertToEntityAttribute(null);
        assertNull(result);
    }

    @Test(expected=IllegalArgumentException.class)
    public void givenWhenInvalidValueIsInDBThenExceptionIsThrown() {
        caseTypeConverter.convertToEntityAttribute(INVALID_VALUE);
    }

}
