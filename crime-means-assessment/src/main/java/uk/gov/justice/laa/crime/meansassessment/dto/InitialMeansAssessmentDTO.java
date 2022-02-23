package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitialMeansAssessmentDTO {

    private String result;
    private String status;
    private String resultReason;
    private BigDecimal annualTotal;
    private BigDecimal adjustedIncomeValue;
    private AssessmentCriteriaEntity assessmentCriteria;
    private ApiCreateMeansAssessmentRequest meansAssessment;
    private List<ApiAssessmentSectionSummary> sectionSummaries;
}
