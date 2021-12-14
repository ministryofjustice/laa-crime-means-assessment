package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

/**
 * static data migrated from TOGDATA.COURT_TYPES table
 */
@AllArgsConstructor
@Getter
public enum CourtType {
    MAGISTRATE("MAGISTRATE", "Magistrate"),
    CROWN_COURT("CROWN COURT", "Crown Court");

    private String courtType;
    private String description;

    @JsonValue
    public String getCourtType() {
        return courtType;
    }

    public static CourtType getFrom(String courtType) throws IllegalArgumentException{
        if (StringUtils.isBlank(courtType)) return null;

        return Stream.of(CourtType.values())
                .filter(f -> f.getCourtType().equals(courtType))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
