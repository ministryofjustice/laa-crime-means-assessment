package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static uk.gov.justice.laa.crime.meansassessment.util.RoundingUtils.setStandardScale;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitMeansAssessmentService implements AssessmentService {

    private final AssessmentCriteriaChildWeightingService childWeightingService;

    @Override
    public MeansAssessmentDTO execute(BigDecimal annualTotal, MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria, boolean isMeansAssessmentRequired) {
        log.info("Create initial means assessment - Start");
        BigDecimal adjustedIncomeValue = getAdjustedIncome(requestDTO, assessmentCriteria, annualTotal);
        CurrentStatus status = requestDTO.getAssessmentStatus();
        log.info("Init means assessment calculation complete for Rep ID: {}", requestDTO.getRepId());
        return MeansAssessmentDTO
                .builder()
                .currentStatus(status)
                .initAssessmentResult(
                        status.equals(CurrentStatus.COMPLETE) ? getResult(
                                adjustedIncomeValue, assessmentCriteria, requestDTO.getNewWorkReason()
                        ) : null
                )
                .adjustedIncomeValue(adjustedIncomeValue)
                .totalAggregatedIncome(annualTotal).build();
    }

    BigDecimal getAdjustedIncome(MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria, BigDecimal annualTotal) {
        BigDecimal totalChildWeighting =
                setStandardScale(childWeightingService.getTotalChildWeighting(requestDTO.getChildWeightings(), assessmentCriteria));

        if (BigDecimal.ZERO.compareTo(annualTotal) <= 0) {
            return setStandardScale(annualTotal)
                    .divide(setStandardScale(assessmentCriteria.getApplicantWeightingFactor())
                                    .add(setStandardScale(assessmentCriteria.getPartnerWeightingFactor()))
                                    .add(totalChildWeighting),
                            RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    InitAssessmentResult getResult(BigDecimal adjustedIncomeValue, AssessmentCriteriaEntity assessmentCriteria, NewWorkReason newWorkReason) {
        BigDecimal lowerThreshold = assessmentCriteria.getInitialLowerThreshold();
        BigDecimal upperThreshold = assessmentCriteria.getInitialUpperThreshold();
        if (adjustedIncomeValue.compareTo(lowerThreshold) <= 0) {
            return InitAssessmentResult.PASS;
        } else if (adjustedIncomeValue.compareTo(upperThreshold) >= 0) {
            // Comment in PL/SQL suggests this should also apply to crown court cases
            if (newWorkReason == NewWorkReason.HR) {
                return InitAssessmentResult.HARDSHIP;
            } else {
                return InitAssessmentResult.FAIL;
            }
        } else {
            return InitAssessmentResult.FULL;
        }
    }
}
