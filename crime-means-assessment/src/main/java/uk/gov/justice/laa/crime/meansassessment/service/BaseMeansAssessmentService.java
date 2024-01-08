package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.util.List;

import static uk.gov.justice.laa.crime.meansassessment.util.RoundingUtils.setStandardScale;

@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public abstract class BaseMeansAssessmentService {

    protected final AssessmentCriteriaService assessmentCriteriaService;

    protected BigDecimal calculateDetailTotal(BigDecimal amount, Frequency frequency) {
        if (amount != null && frequency != null && !BigDecimal.ZERO.equals(amount)) {
            return setStandardScale(BigDecimal.valueOf(frequency.getWeighting()).multiply(amount));
        } else return setStandardScale(BigDecimal.ZERO);
    }

    protected BigDecimal calculateSummariesTotal(final MeansAssessmentRequestDTO requestDTO,
                                                 final AssessmentCriteriaEntity assessmentCriteria) {

        List<ApiAssessmentSectionSummary> sectionSummaries = requestDTO.getSectionSummaries();
        BigDecimal annualTotal = BigDecimal.ZERO;
        for (ApiAssessmentSectionSummary sectionSummary : sectionSummaries) {
            BigDecimal summaryTotal;
            BigDecimal applicantTotal;
            BigDecimal partnerTotal;

            applicantTotal = partnerTotal = BigDecimal.ZERO;
            for (ApiAssessmentDetail assessmentDetail : sectionSummary.getAssessmentDetails()) {
                assessmentCriteriaService.checkAssessmentDetail(
                        requestDTO.getCaseType(), sectionSummary.getSection(), assessmentCriteria, assessmentDetail
                );

                applicantTotal = applicantTotal.add(
                        calculateDetailTotal(assessmentDetail.getApplicantAmount(), assessmentDetail.getApplicantFrequency()));

                partnerTotal = partnerTotal.add(
                        calculateDetailTotal(assessmentDetail.getPartnerAmount(), assessmentDetail.getPartnerFrequency()));

            }
            summaryTotal = applicantTotal.add(partnerTotal);
            sectionSummary.setApplicantAnnualTotal(applicantTotal);
            sectionSummary.setAnnualTotal(summaryTotal);
            sectionSummary.setPartnerAnnualTotal(partnerTotal);

            annualTotal = annualTotal.add(summaryTotal);
        }
        return annualTotal;
    }
}
