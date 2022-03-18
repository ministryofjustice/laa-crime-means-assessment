package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FullMeansAssessmentService implements AssessmentService {

    private final AssessmentCriteriaChildWeightingService childWeightingService;

    public MeansAssessmentDTO execute(BigDecimal expenditureTotal, ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria) {
        log.info("Create full means assessment - Start");
        BigDecimal adjustedLivingAllowance = getAdjustedLivingAllowance(meansAssessment, assessmentCriteria);
        BigDecimal totalDisposableIncome = getDisposableIncome(meansAssessment, expenditureTotal, adjustedLivingAllowance);
        CurrentStatus status = meansAssessment.getAssessmentStatus();
        log.info("Full means assessment calculation complete for Rep ID: {}", meansAssessment.getRepId());
        return MeansAssessmentDTO
                .builder()
                .currentStatus(status)
                .fullAssessmentResult(
                        status.equals(CurrentStatus.COMPLETE) ? getResult(
                                totalDisposableIncome, assessmentCriteria
                        ) : FullAssessmentResult.NONE
                )
                .adjustedLivingAllowance(adjustedLivingAllowance)
                .totalAggregatedExpense(expenditureTotal)
                .totalAnnualDisposableIncome(totalDisposableIncome).build();
    }

    BigDecimal getDisposableIncome(ApiCreateMeansAssessmentRequest meansAssessment, BigDecimal expenditureTotal, BigDecimal adjustedLivingAllowance) {
        return meansAssessment.getInitTotalAggregatedIncome().subtract(
                expenditureTotal.add(
                        adjustedLivingAllowance
                )
        );
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

    FullAssessmentResult getResult(BigDecimal disposableIncome, AssessmentCriteriaEntity assessmentCriteria) {
        if (disposableIncome.compareTo(assessmentCriteria.getFullThreshold()) <= 0) {
            return FullAssessmentResult.PASS;
        } else {
            return FullAssessmentResult.FAIL;
        }
    }
}
