package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.Before;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FrequencyConverterTest {
    private FrequencyConverter frequencyConverter;

    @Before
    public void init(){
        frequencyConverter = new FrequencyConverter();
    }

    @Test
    public void convertToDatabaseColumn_success() {
        var dbValueReturned = frequencyConverter.convertToDatabaseColumn(Frequency.FOUR_WEEKLY);
        assertEquals(Frequency.FOUR_WEEKLY.getCode(), dbValueReturned);
    }
    @Test
    public void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = frequencyConverter.convertToDatabaseColumn(null);
        assertNull(nullValueReturned);
    }

    @Test
    public void convertToFrequency_success(){
        var frequencyReturned = frequencyConverter.convertToEntityAttribute("4WEEKLY");
        assertEquals(Frequency.FOUR_WEEKLY, frequencyReturned);
    }
    @Test
    public void convertToFrequency_nullValueReturned(){
        var nullValueReturned = frequencyConverter.convertToEntityAttribute(null);
        assertNull(nullValueReturned);
    }

    @Test(expected=IllegalArgumentException.class)
    public void convertToFrequency_valueNotFound_throwsException() {
        frequencyConverter.convertToEntityAttribute("THROWS_EXCEPTION");
    }
}