package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;

@Component
@AllArgsConstructor
public class MeansAssessmentRequestDTOBuilder {

    public MeansAssessmentRequestDTO buildRequestDTO(final ApiMeansAssessmentRequest assessmentRequest) {

        MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO.builder()
                .repId(assessmentRequest.getRepId())
                .cmuId(assessmentRequest.getCmuId())
                .initialAssessmentDate(assessmentRequest.getInitialAssessmentDate())
                .otherBenefitNote(assessmentRequest.getOtherBenefitNote())
                .otherIncomeNote(assessmentRequest.getOtherIncomeNote())
                .employmentStatus(assessmentRequest.getEmploymentStatus())
                .initAssessmentNotes(assessmentRequest.getInitAssessmentNotes())
                .assessmentStatus(assessmentRequest.getAssessmentStatus())
                .sectionSummaries(assessmentRequest.getSectionSummaries())
                .childWeightings(assessmentRequest.getChildWeightings())
                .hasPartner(assessmentRequest.getHasPartner())
                .partnerContraryInterest(assessmentRequest.getPartnerContraryInterest())
                .assessmentType(assessmentRequest.getAssessmentType())
                .caseType(assessmentRequest.getCaseType())
                .userSession(assessmentRequest.getUserSession())
                .incomeEvidenceSummary(assessmentRequest.getIncomeEvidenceSummary())
                .crownCourtOverview(assessmentRequest.getCrownCourtOverview())
                .magCourtOutcome(assessmentRequest.getMagCourtOutcome())
                .newWorkReason(assessmentRequest.getNewWorkReason())
                .build();

        if (assessmentRequest instanceof ApiCreateMeansAssessmentRequest createMeansAssessmentRequest) {
            requestDTO.setUsn(createMeansAssessmentRequest.getUsn());
            requestDTO.setReviewType(createMeansAssessmentRequest.getReviewType());
        }

        if (assessmentRequest instanceof ApiUpdateMeansAssessmentRequest updateMeansAssessmentRequest) {
            requestDTO.setFullAssessmentDate(updateMeansAssessmentRequest.getFullAssessmentDate());
            requestDTO.setOtherHousingNote(updateMeansAssessmentRequest.getOtherHousingNote());
            requestDTO.setInitTotalAggregatedIncome(updateMeansAssessmentRequest.getInitTotalAggregatedIncome());
            requestDTO.setFullAssessmentNotes(updateMeansAssessmentRequest.getFullAssessmentNotes());
            requestDTO.setFinancialAssessmentId(updateMeansAssessmentRequest.getFinancialAssessmentId());
            requestDTO.setIncomeEvidence(updateMeansAssessmentRequest.getIncomeEvidence());
        }

        return requestDTO;
    }
}