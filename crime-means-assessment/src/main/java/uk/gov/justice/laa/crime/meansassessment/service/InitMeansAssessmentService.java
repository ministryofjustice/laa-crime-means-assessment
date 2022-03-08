package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitialAssessmentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitMeansAssessmentService {

    private final AssessmentCriteriaChildWeightingService childWeightingService;

    protected BigDecimal getAdjustedIncome(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria, BigDecimal annualTotal) {
        BigDecimal totalChildWeighting =
                childWeightingService.getTotalChildWeighting(meansAssessment.getChildWeightings(), assessmentCriteria);
        if (BigDecimal.ZERO.compareTo(annualTotal) <= 0) {
            return annualTotal
                    .divide(assessmentCriteria.getApplicantWeightingFactor()
                                    .add(assessmentCriteria.getPartnerWeightingFactor())
                                    .add(totalChildWeighting),
                            RoundingMode.UP);
        }
        return BigDecimal.ZERO;
    }

    protected InitialAssessmentResult getAssessmentResult(BigDecimal adjustedIncomeValue, AssessmentCriteriaEntity assessmentCriteria, String newWorkReasonCode) {
        BigDecimal lowerThreshold = assessmentCriteria.getInitialLowerThreshold();
        BigDecimal upperThreshold = assessmentCriteria.getInitialUpperThreshold();
        if (adjustedIncomeValue.compareTo(lowerThreshold) <= 0) {
            return InitialAssessmentResult.PASS;
        } else if (adjustedIncomeValue.compareTo(upperThreshold) >= 0) {
            // TODO: Comment in PL/SQL suggests this should also apply to crown court cases
            if (newWorkReasonCode.equalsIgnoreCase("HR")) {
                return InitialAssessmentResult.HARDSHIP;
            } else {
                return InitialAssessmentResult.FAIL;
            }
        } else {
            return InitialAssessmentResult.FULL;
        }
    }
}
