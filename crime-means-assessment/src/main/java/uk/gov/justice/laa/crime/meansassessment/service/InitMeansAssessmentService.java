package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitMeansAssessmentService implements AssessmentService {

    private final AssessmentCriteriaChildWeightingService childWeightingService;

    public MeansAssessmentDTO execute(BigDecimal annualTotal, ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria) {
        log.info("Create initial means assessment - Start");
        BigDecimal adjustedIncomeValue = getAdjustedIncome(meansAssessment, assessmentCriteria, annualTotal);
        CurrentStatus status = meansAssessment.getAssessmentStatus();
        log.info("Init means assessment calculation complete for Rep ID: {}", meansAssessment.getRepId());
        return MeansAssessmentDTO
                .builder()
                .currentStatus(status)
                .initAssessmentResult(
                        status.equals(CurrentStatus.COMPLETE) ? getResult(
                                adjustedIncomeValue, assessmentCriteria, meansAssessment.getNewWorkReason().getCode()
                        ) : InitAssessmentResult.NONE
                )
                .adjustedIncomeValue(adjustedIncomeValue)
                .totalAggregatedIncome(annualTotal).build();
    }

    BigDecimal getAdjustedIncome(ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria, BigDecimal annualTotal) {
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

    InitAssessmentResult getResult(BigDecimal adjustedIncomeValue, AssessmentCriteriaEntity assessmentCriteria, String newWorkReasonCode) {
        BigDecimal lowerThreshold = assessmentCriteria.getInitialLowerThreshold();
        BigDecimal upperThreshold = assessmentCriteria.getInitialUpperThreshold();
        if (adjustedIncomeValue.compareTo(lowerThreshold) <= 0) {
            return InitAssessmentResult.PASS;
        } else if (adjustedIncomeValue.compareTo(upperThreshold) >= 0) {
            // TODO: Comment in PL/SQL suggests this should also apply to crown court cases
            if (newWorkReasonCode.equalsIgnoreCase("HR")) {
                return InitAssessmentResult.HARDSHIP;
            } else {
                return InitAssessmentResult.FAIL;
            }
        } else {
            return InitAssessmentResult.FULL;
        }
    }
}
