package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import uk.gov.justice.laa.crime.enums.CurrentStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CurrentStatusConverterTest {

    private CurrentStatusConverter currentStatusConverter;

    @BeforeEach
    void init() {
        currentStatusConverter = new CurrentStatusConverter();
    }

    @Test
    void convertToDatabaseColumn_success() {
        var dbValueReturned = currentStatusConverter.convertToDatabaseColumn(CurrentStatus.IN_PROGRESS);
        assertThat(dbValueReturned).isEqualTo(CurrentStatus.IN_PROGRESS.getStatus());
    }

    @Test
    void convertToDatabaseColumn_expectsNull() {
        var nullValueReturned = currentStatusConverter.convertToDatabaseColumn(null);
        assertThat(nullValueReturned).isNull();
    }

    @Test
    void convertToCurrentStatus_success() {
        var currentStatusReturned = currentStatusConverter.convertToEntityAttribute("IN PROGRESS");
        assertThat(currentStatusReturned).isEqualTo(CurrentStatus.IN_PROGRESS);
    }

    @Test
    void convertToCurrentStatus_nullValueReturned() {
        var nullValueReturned = currentStatusConverter.convertToEntityAttribute(null);
        assertThat(nullValueReturned).isNull();
    }

    @Test
    void convertToCurrentStatus_valueNotFound_throwsException() {
        assertThatThrownBy(() -> currentStatusConverter.convertToEntityAttribute("THROWS_EXCEPTION"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
