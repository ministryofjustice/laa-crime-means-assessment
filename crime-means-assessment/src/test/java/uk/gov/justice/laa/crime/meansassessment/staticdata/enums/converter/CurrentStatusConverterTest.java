package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import static org.junit.jupiter.api.Assertions.*;

class CurrentStatusConverterTest {

    private CurrentStatusConverter currentStatusConverter;

    @BeforeEach
    void init() {
        currentStatusConverter = new CurrentStatusConverter();
    }

    @Test
    void convertToDatabaseColumn_success() {
        var dbValueReturned = currentStatusConverter.convertToDatabaseColumn(CurrentStatus.IN_PROGRESS);
        assertEquals(CurrentStatus.IN_PROGRESS.getStatus(), dbValueReturned);
    }

    @Test
    void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = currentStatusConverter.convertToDatabaseColumn(null);
        assertNull(nullValueReturned);
    }

    @Test
    void convertToCurrentStatus_success() {
        var currentStatusReturned = currentStatusConverter.convertToEntityAttribute("IN PROGRESS");
        assertEquals(CurrentStatus.IN_PROGRESS, currentStatusReturned);
    }

    @Test
    void convertToCurrentStatus_nullValueReturned() {
        var nullValueReturned = currentStatusConverter.convertToEntityAttribute(null);
        assertNull(nullValueReturned);
    }

    @Test
    void convertToCurrentStatus_valueNotFound_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> currentStatusConverter.convertToEntityAttribute("THROWS_EXCEPTION"));
    }
}