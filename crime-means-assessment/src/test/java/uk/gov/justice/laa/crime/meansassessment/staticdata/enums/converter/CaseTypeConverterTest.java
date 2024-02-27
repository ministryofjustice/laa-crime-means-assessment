package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.enums.CaseType;

import static org.junit.jupiter.api.Assertions.*;

class CaseTypeConverterTest {

    private static final String VALID_VALUE = "APPEAL CC";
    private static final String INVALID_VALUE = "INVALID_VALUE";

    private final CaseTypeConverter caseTypeConverter = new CaseTypeConverter();

    @Test
    void givenWhenEnumIsProvidedThenMatchingValueForDBIsReturned() {
        String result = caseTypeConverter.convertToDatabaseColumn(CaseType.APPEAL_CC);
        assertEquals(CaseType.APPEAL_CC.getCaseType(), result);
    }

    @Test
    void givenWhenValidValueIsInDBThenCorrectEnumIsReturned() {
        CaseType result = caseTypeConverter.convertToEntityAttribute(VALID_VALUE);
        assertEquals(CaseType.APPEAL_CC, result);
    }

    @Test
    void givenWhenEnumIsNullThenNullDBValueReturned() {
        String result = caseTypeConverter.convertToDatabaseColumn(null);
        assertNull(result);
    }

    @Test
    void givenWhenDBValueIsNullThenNoEnumIsReturned() {
        CaseType result = caseTypeConverter.convertToEntityAttribute(null);
        assertNull(result);
    }

    @Test
    void givenWhenInvalidValueIsInDBThenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class,
                () -> caseTypeConverter.convertToEntityAttribute(INVALID_VALUE));
    }

}
