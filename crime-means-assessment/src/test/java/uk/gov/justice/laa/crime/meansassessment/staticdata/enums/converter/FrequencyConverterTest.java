package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import static org.junit.jupiter.api.Assertions.*;

class FrequencyConverterTest {
    private FrequencyConverter frequencyConverter;

    @BeforeEach
    void init() {
        frequencyConverter = new FrequencyConverter();
    }

    @Test
    void convertToDatabaseColumn_success() {
        var dbValueReturned = frequencyConverter.convertToDatabaseColumn(Frequency.FOUR_WEEKLY);
        assertEquals(Frequency.FOUR_WEEKLY.getCode(), dbValueReturned);
    }

    @Test
    void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = frequencyConverter.convertToDatabaseColumn(null);
        assertNull(nullValueReturned);
    }

    @Test
    void convertToFrequency_success() {
        var frequencyReturned = frequencyConverter.convertToEntityAttribute("4WEEKLY");
        assertEquals(Frequency.FOUR_WEEKLY, frequencyReturned);
    }

    @Test
    void convertToFrequency_nullValueReturned() {
        var nullValueReturned = frequencyConverter.convertToEntityAttribute(null);
        assertNull(nullValueReturned);
    }

    @Test
    void convertToFrequency_valueNotFound_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> frequencyConverter.convertToEntityAttribute("THROWS_EXCEPTION"));
    }
}