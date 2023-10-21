package uk.gov.justice.laa.crime.meansassessment.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class DateUtil {

    private DateUtil() {
    }

    public static LocalDateTime getLocalDateTime(final String date) {
        return date != null ? LocalDate.parse(date).atTime(0, 0) : null;
    }

}
