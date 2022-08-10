package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;

@Component
@AllArgsConstructor
public class MeansAssessmentRequestDTOBuilder {

    public MeansAssessmentRequestDTO buildRequestDTO(final ApiMeansAssessmentRequest assessmentRequest) {

        MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO.builder()
                .laaTransactionId(assessmentRequest.getLaaTransactionId())
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
                .build();

        if (assessmentRequest instanceof ApiCreateMeansAssessmentRequest) {
            ApiCreateMeansAssessmentRequest initMeansAssessmentRequest = (ApiCreateMeansAssessmentRequest) assessmentRequest;
            requestDTO.setUsn(initMeansAssessmentRequest.getUsn());
            requestDTO.setReviewType(initMeansAssessmentRequest.getReviewType());
            requestDTO.setNewWorkReason(initMeansAssessmentRequest.getNewWorkReason());
        }

        if (assessmentRequest instanceof ApiUpdateMeansAssessmentRequest) {
            ApiUpdateMeansAssessmentRequest fullMeansAssessmentRequest = (ApiUpdateMeansAssessmentRequest) assessmentRequest;
            requestDTO.setFullAssessmentDate(fullMeansAssessmentRequest.getFullAssessmentDate());
            requestDTO.setOtherHousingNote(fullMeansAssessmentRequest.getOtherHousingNote());
            requestDTO.setInitTotalAggregatedIncome(fullMeansAssessmentRequest.getInitTotalAggregatedIncome());
            requestDTO.setFullAssessmentNotes(fullMeansAssessmentRequest.getFullAssessmentNotes());
        }

        return requestDTO;
    }
}