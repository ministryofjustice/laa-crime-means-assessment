package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class StatelessFullResult {
    private final FullAssessmentResult result;
    private final BigDecimal disposableIncome;
    private final BigDecimal adjustedIncomeValue;
    private final BigDecimal totalAggregatedIncome;
    private final BigDecimal adjustedLivingAllowance;
    private final BigDecimal totalAnnualAggregatedExpenditure;
    private final BigDecimal eligibilityThreshold;

    public String getResultReason() {
        return result.getReason();
    }
}

