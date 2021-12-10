package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum HardshipReviewStatus {
    IN_PROGRESS("IN PROGRESS", "Incomplete"),
    COMPLETE("COMPLETE","Complete");

    private String status;
    private String description;

    /*mapping json to HardshipReviewStatus object*/
    @JsonValue
    public String getStatus(){ return status; }

    public static HardshipReviewStatus getFrom(String status) throws IllegalArgumentException{
        if (StringUtils.isBlank(status)) return null;

        return Stream.of(HardshipReviewStatus.values())
                .filter(f -> f.status.equals(status))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
