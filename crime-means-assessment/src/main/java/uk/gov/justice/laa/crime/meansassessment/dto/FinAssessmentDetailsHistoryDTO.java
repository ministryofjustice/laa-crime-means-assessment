package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;

@Data
@Builder
public class FinAssessmentDetailsHistoryDTO {
    private Integer criteriaDetailId;
    private BigDecimal applicantAmount;
    private Frequency applicantFrequency;
    private BigDecimal partnerAmount;
    private Frequency partnerFrequency;
    private Integer finAssessmentDetailId;
    private String userCreated;
}
