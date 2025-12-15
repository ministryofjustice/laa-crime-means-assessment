package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import uk.gov.justice.laa.crime.enums.Frequency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FrequencyConverterTest {
    private FrequencyConverter frequencyConverter;

    @BeforeEach
    void init() {
        frequencyConverter = new FrequencyConverter();
    }

    @Test
    void convertToDatabaseColumn_success() {
        var dbValueReturned = frequencyConverter.convertToDatabaseColumn(Frequency.FOUR_WEEKLY);
        assertThat(dbValueReturned).isEqualTo(Frequency.FOUR_WEEKLY.getCode());
    }

    @Test
    void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = frequencyConverter.convertToDatabaseColumn(null);
        assertThat(nullValueReturned).isNull();
    }

    @Test
    void convertToFrequency_success() {
        var frequencyReturned = frequencyConverter.convertToEntityAttribute("4WEEKLY");
        assertThat(frequencyReturned).isEqualTo(Frequency.FOUR_WEEKLY);
    }

    @Test
    void convertToFrequency_nullValueReturned() {
        var nullValueReturned = frequencyConverter.convertToEntityAttribute(null);
        assertThat(nullValueReturned).isNull();
    }

    @Test
    void convertToFrequency_valueNotFound_throwsException() {
        assertThatThrownBy(() -> frequencyConverter.convertToEntityAttribute("THROWS_EXCEPTION"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
