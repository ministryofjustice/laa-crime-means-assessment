package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class FullMeansAssessmentService implements AssessmentService {

    private final AssessmentCriteriaChildWeightingService childWeightingService;

    public MeansAssessmentDTO execute(BigDecimal expenditureTotal, MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria) {
        log.info("Create full means assessment - Start");
        BigDecimal adjustedLivingAllowance = getAdjustedLivingAllowance(requestDTO, assessmentCriteria);
        BigDecimal totalDisposableIncome = getDisposableIncome(requestDTO, expenditureTotal, adjustedLivingAllowance);
        CurrentStatus status = requestDTO.getAssessmentStatus();
        log.info("Full means assessment calculation complete for Rep ID: {}", requestDTO.getRepId());
        return MeansAssessmentDTO
                .builder()
                .currentStatus(status)
                .fullAssessmentResult(
                        status.equals(CurrentStatus.COMPLETE) ? getResult(
                                totalDisposableIncome, assessmentCriteria
                        ) : null
                )
                .adjustedLivingAllowance(adjustedLivingAllowance)
                .totalAggregatedExpense(expenditureTotal)
                .totalAnnualDisposableIncome(totalDisposableIncome).build();
    }

    BigDecimal getDisposableIncome(MeansAssessmentRequestDTO requestDTO, BigDecimal expenditureTotal, BigDecimal adjustedLivingAllowance) {
        return requestDTO.getInitTotalAggregatedIncome()
                .subtract(
                        expenditureTotal.add(
                                adjustedLivingAllowance
                        )
                );
    }

    BigDecimal getAdjustedLivingAllowance(MeansAssessmentRequestDTO requestDTO, AssessmentCriteriaEntity assessmentCriteria) {
        BigDecimal totalChildWeighting =
                childWeightingService.getTotalChildWeighting(requestDTO.getChildWeightings(), assessmentCriteria);

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
