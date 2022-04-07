package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinAssessmentChildWeightHistoryDTO {
    private Integer finAssChildWeightingId;
    private Integer weightingId;
    private Integer noOfChildren;
    private String userCreated;
}
