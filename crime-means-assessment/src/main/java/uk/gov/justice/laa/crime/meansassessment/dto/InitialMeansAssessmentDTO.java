package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitialMeansAssessmentDTO {
    private BigDecimal annualTotal;
    private CurrentStatus initStatus;
    private BigDecimal adjustedIncomeValue;
    private MeansAssessmentResultDTO assessmentResult;
    private AssessmentCriteriaEntity assessmentCriteria;
    private ApiCreateMeansAssessmentRequest meansAssessment;
    private List<ApiAssessmentSectionSummary> sectionSummaries;
}
