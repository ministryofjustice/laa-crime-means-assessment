package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;
/**
 * static data migrated from TOGDATA.HARDSHIP_REVIEW_STATUSES,
 * TOGDATA.IOJ_APPEAL_STATUSES and TOGDATA.FIN_ASS_STATUSES tables
 */
@AllArgsConstructor
@Getter
public enum CurrentStatus {
    IN_PROGRESS("IN PROGRESS", "Incomplete"),
    COMPLETE("COMPLETE","Complete");

    private String status;
    private String description;

    /*mapping json to CurrentStatus object*/
    @JsonValue
    public String getStatus(){ return status; }

    public static CurrentStatus getFrom(String status) throws IllegalArgumentException{
        if (StringUtils.isBlank(status)) return null;

        return Stream.of(CurrentStatus.values())
                .filter(f -> f.status.equals(status))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}