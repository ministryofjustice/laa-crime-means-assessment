package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FullMeansAssessmentService {

    private final AssessmentCriteriaChildWeightingService childWeightingService;

    BigDecimal getDisposableIncome(ApiCreateMeansAssessmentRequest meansAssessment, BigDecimal expenditureTotal, BigDecimal adjustedLivingAllowance) {
        BigDecimal totalDisposableIncome =
                meansAssessment.getInitTotalAggregatedIncome().subtract(
                        expenditureTotal.subtract(
                                adjustedLivingAllowance
                        )
                );
        return meansAssessment.getInitTotalAggregatedIncome().subtract(totalDisposableIncome);
    }

    BigDecimal getAdjustedLivingAllowance(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria) {
        BigDecimal totalChildWeighting =
                childWeightingService.getTotalChildWeighting(meansAssessment.getChildWeightings(), assessmentCriteria);

        return assessmentCriteria.getLivingAllowance().multiply(
                assessmentCriteria.getApplicantWeightingFactor().add(
                        assessmentCriteria.getPartnerWeightingFactor().add(
                                totalChildWeighting
                        )
                )
        );
    }

    FullAssessmentResult getAssessmentResult(BigDecimal disposableIncome, AssessmentCriteriaEntity assessmentCriteria) {
        if (disposableIncome.compareTo(assessmentCriteria.getFullThreshold()) <= 0) {
            return FullAssessmentResult.PASS;
        } else {
            return FullAssessmentResult.FAIL;
        }
    }
}
