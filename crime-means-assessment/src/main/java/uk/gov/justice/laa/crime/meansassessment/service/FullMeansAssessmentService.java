package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.FullAssessmentResult;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;

import static uk.gov.justice.laa.crime.meansassessment.service.CrownCourtEligibilityService.isCrownCourtCase;
import static uk.gov.justice.laa.crime.meansassessment.util.RoundingUtils.setStandardScale;

@Slf4j
@Service
@RequiredArgsConstructor
public class FullMeansAssessmentService implements AssessmentService {

    private final AssessmentCriteriaChildWeightingService childWeightingService;

    @Override
    public MeansAssessmentDTO execute(BigDecimal expenditureTotal, MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria) {
        log.info("Create full means assessment - Start");
        CurrentStatus status = requestDTO.getAssessmentStatus();
        BigDecimal totalAggregatedIncome = requestDTO.getInitTotalAggregatedIncome();
        BigDecimal adjustedLivingAllowance = getAdjustedLivingAllowance(requestDTO, assessmentCriteria);
        BigDecimal totalDisposableIncome = getDisposableIncome(totalAggregatedIncome, expenditureTotal, adjustedLivingAllowance);
        log.info("Full means assessment calculation complete for Rep ID: {}", requestDTO.getRepId());
        return MeansAssessmentDTO
                .builder()
                .currentStatus(status)
                .fullAssessmentResult(
                        status.equals(CurrentStatus.COMPLETE)
                                ? getResult(totalDisposableIncome, requestDTO, assessmentCriteria) : null
                )
                .adjustedLivingAllowance(adjustedLivingAllowance)
                .totalAggregatedExpense(
                        getAnnualAggregatedExpenditure(totalAggregatedIncome, totalDisposableIncome)
                )
                .totalAnnualDisposableIncome(totalDisposableIncome).build();
    }

    BigDecimal getAnnualAggregatedExpenditure(BigDecimal totalAggregatedIncome, BigDecimal totalDisposableIncome) {
        return setStandardScale(totalAggregatedIncome.subtract(totalDisposableIncome));
    }

    BigDecimal getDisposableIncome(BigDecimal totalAggregatedIncome, BigDecimal expenditureTotal, BigDecimal adjustedLivingAllowance) {
        return setStandardScale(totalAggregatedIncome.subtract(expenditureTotal.add(adjustedLivingAllowance)));
    }

    BigDecimal getAdjustedLivingAllowance(MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria) {
        BigDecimal totalChildWeighting =
                setStandardScale(
                        childWeightingService.getTotalChildWeighting(
                                requestDTO.getChildWeightings(), assessmentCriteria
                        )
                );

        return setStandardScale(assessmentCriteria.getLivingAllowance())
                .multiply(setStandardScale(assessmentCriteria.getApplicantWeightingFactor())
                        .add(setStandardScale(assessmentCriteria.getPartnerWeightingFactor()))
                        .add(totalChildWeighting));
    }

    FullAssessmentResult getResult(BigDecimal disposableIncome, MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria) {
        if (isCrownCourtCase(requestDTO.getCaseType(), requestDTO.getMagCourtOutcome()) &&
                requestDTO.isEligibilityCheckRequired()
                && disposableIncome.compareTo(assessmentCriteria.getEligibilityThreshold()) >= 0) {
            return FullAssessmentResult.INEL;
        } else if (disposableIncome.compareTo(assessmentCriteria.getFullThreshold()) <= 0) {
            return FullAssessmentResult.PASS;
        } else {
            return FullAssessmentResult.FAIL;
        }
    }
}
