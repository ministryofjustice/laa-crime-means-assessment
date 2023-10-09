package uk.gov.justice.laa.crime.meansassessment.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class DateUtilTest {

    @Test
    void givenAValidLocalDate_whenGetLocalDateTimeIsInvoked_thenReturnDateTime() {
        String dateModified = "2022-12-09";
        assertThat(DateUtil.getLocalDateTime(dateModified)).isNotNull();
    }

    @Test
    void givenAEmptyLocalDate_whenGetLocalDateTimeIsInvoked_thenReturnNull() {
        assertThat(DateUtil.getLocalDateTime(null)).isNull();
    }
}