package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.math.BigDecimal;

@Data
@Builder
public class MeansAssessmentDTO {
    private String userCreated;
    private CurrentStatus currentStatus;
    private BigDecimal adjustedIncomeValue;
    private BigDecimal totalAggregatedIncome;
    private BigDecimal totalAggregatedExpense;
    private BigDecimal adjustedLivingAllowance;
    private AssessmentCriteriaEntity assessmentCriteria;
    private ApiCreateMeansAssessmentRequest meansAssessment;
    private BigDecimal totalAnnualDisposableIncome;
    private InitAssessmentResult initAssessmentResult;
    private FullAssessmentResult fullAssessmentResult;
}