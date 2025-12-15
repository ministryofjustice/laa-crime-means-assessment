package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import uk.gov.justice.laa.crime.enums.CaseType;

import org.junit.jupiter.api.Test;

class CaseTypeConverterTest {

    private static final String VALID_VALUE = "APPEAL CC";
    private static final String INVALID_VALUE = "INVALID_VALUE";

    private final CaseTypeConverter caseTypeConverter = new CaseTypeConverter();

    @Test
    void givenWhenEnumIsProvidedThenMatchingValueForDBIsReturned() {
        String result = caseTypeConverter.convertToDatabaseColumn(CaseType.APPEAL_CC);
        assertThat(result).isEqualTo(CaseType.APPEAL_CC.getCaseType());
    }

    @Test
    void givenWhenValidValueIsInDBThenCorrectEnumIsReturned() {
        CaseType result = caseTypeConverter.convertToEntityAttribute(VALID_VALUE);
        assertThat(result).isEqualTo(CaseType.APPEAL_CC);
    }

    @Test
    void givenWhenEnumIsNullThenNullDBValueReturned() {
        String result = caseTypeConverter.convertToDatabaseColumn(null);
        assertThat(result).isNull();
    }

    @Test
    void givenWhenDBValueIsNullThenNoEnumIsReturned() {
        CaseType result = caseTypeConverter.convertToEntityAttribute(null);
        assertThat(result).isNull();
    }

    @Test
    void givenWhenInvalidValueIsInDBThenExceptionIsThrown() {
        assertThatThrownBy(() -> caseTypeConverter.convertToEntityAttribute(INVALID_VALUE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
