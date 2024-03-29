package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.FullAssessmentResult;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private MeansAssessmentRequestDTO meansAssessment;
    private BigDecimal totalAnnualDisposableIncome;
    private InitAssessmentResult initAssessmentResult;
    private FullAssessmentResult fullAssessmentResult;
    private LocalDateTime dateCompleted;
    private LocalDateTime applicationTimestamp;
}