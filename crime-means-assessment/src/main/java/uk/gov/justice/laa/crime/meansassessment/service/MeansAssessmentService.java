package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.builder.CreateInitialAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.InitialMeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitialAssessmentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final AssessmentCriteriaService assessmentCriteriaService;
    private final CreateInitialAssessmentBuilder createInitialAssessmentBuilder;
    private final AssessmentCriteriaChildWeightingService childWeightingService;
    private final CourtDataService courtDataService;

    public ApiCreateMeansAssessmentResponse createMeansAssessment(ApiCreateMeansAssessmentRequest meansAssessment) {
        log.info("Create means assessment - Start");
        List<ApiAssessmentSectionSummary> sectionSummaries = meansAssessment.getSectionSummaries();
        AssessmentCriteriaEntity assessmentCriteria =
                assessmentCriteriaService.getAssessmentCriteria(
                        meansAssessment.getAssessmentDate(), meansAssessment.getHasPartner(), meansAssessment.getPartnerContraryInterest()
                );

        BigDecimal annualTotal = getAnnualTotal(meansAssessment.getCaseType(), assessmentCriteria, sectionSummaries);
        BigDecimal adjustedIncomeValue = getAdjustedIncome(meansAssessment, assessmentCriteria, annualTotal);

        InitialAssessmentResult result;
        CurrentStatus status = meansAssessment.getAssessmentStatus();
        String newWorkReasonCode = meansAssessment.getNewWorkReason().getCode();
        if (status.equals(CurrentStatus.COMPLETE)) {
            result = getAssessmentResult(adjustedIncomeValue, assessmentCriteria, newWorkReasonCode);
        } else {
            result = InitialAssessmentResult.NONE;
        }

        log.info("Initial means assessment calculation complete for Rep ID: {}", meansAssessment.getRepId());

        ApiCreateAssessment assessment = createInitialAssessmentBuilder.build(
                new InitialMeansAssessmentDTO(annualTotal, status, adjustedIncomeValue, result, assessmentCriteria, meansAssessment, sectionSummaries));
        return courtDataService.postMeansAssessment(assessment, meansAssessment.getLaaTransactionId());
    }

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
        if (adjustedIncomeValue.compareTo(assessmentCriteria.getInitialLowerThreshold()) <= 0) {
            return InitialAssessmentResult.PASS;
        } else if (adjustedIncomeValue.compareTo(assessmentCriteria.getInitialUpperThreshold()) >= 0) {
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

    protected BigDecimal getAnnualTotal(CaseType caseType, AssessmentCriteriaEntity assessmentCriteria, List<ApiAssessmentSectionSummary> sectionSummaries) {
        BigDecimal annualTotal = BigDecimal.ZERO;
        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            for (ApiAssessmentDetail assessmentDetail : sectionSummary.getAssessmentDetails()) {
                assessmentCriteriaService.checkAssessmentDetail(caseType, sectionSummary.getSection(), assessmentCriteria, assessmentDetail);
                annualTotal = annualTotal.add(getDetailTotal(assessmentDetail));
            }
        }
        return annualTotal;
    }

    protected BigDecimal getDetailTotal(ApiAssessmentDetail assessmentDetail) {
        BigDecimal detailTotal = BigDecimal.ZERO;
        BigDecimal partnerAmount = assessmentDetail.getPartnerAmount();
        if (partnerAmount != null && !BigDecimal.ZERO.equals(partnerAmount)) {
            detailTotal = detailTotal.add(
                    partnerAmount.multiply(
                            BigDecimal.valueOf(assessmentDetail.getPartnerFrequency().getWeighting())
                    )
            );
        }
        BigDecimal applicationAmount = assessmentDetail.getApplicantAmount();
        if (applicationAmount != null && !BigDecimal.ZERO.equals(applicationAmount)) {
            detailTotal = detailTotal.add(
                    applicationAmount.multiply(
                            BigDecimal.valueOf(assessmentDetail.getApplicantFrequency().getWeighting())
                    )
            );
        }
        return detailTotal;
    }
}
