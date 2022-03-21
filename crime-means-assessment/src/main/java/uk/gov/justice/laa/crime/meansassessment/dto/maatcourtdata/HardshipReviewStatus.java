package uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HardshipReviewStatus {

    IN_PROGRESS("IN PROGRESS", "Incomplete"),
    COMPLETE("COMPLETE", "Complete");

    private String status;
    private String description;

}
