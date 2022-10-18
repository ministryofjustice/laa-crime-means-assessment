package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiUpdateMeansAssessmentRequest;

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
                .newWorkReason(assessmentRequest.getNewWorkReason())
                .build();

        if (assessmentRequest instanceof ApiCreateMeansAssessmentRequest) {
            ApiCreateMeansAssessmentRequest createMeansAssessmentRequest = (ApiCreateMeansAssessmentRequest) assessmentRequest;
            requestDTO.setUsn(createMeansAssessmentRequest.getUsn());
            requestDTO.setReviewType(createMeansAssessmentRequest.getReviewType());
        }

        if (assessmentRequest instanceof ApiUpdateMeansAssessmentRequest) {
            ApiUpdateMeansAssessmentRequest updateMeansAssessmentRequest = (ApiUpdateMeansAssessmentRequest) assessmentRequest;
            requestDTO.setTimeStamp(updateMeansAssessmentRequest.getTimestamp());
            requestDTO.setFullAssessmentDate(updateMeansAssessmentRequest.getFullAssessmentDate());
            requestDTO.setOtherHousingNote(updateMeansAssessmentRequest.getOtherHousingNote());
            requestDTO.setInitTotalAggregatedIncome(updateMeansAssessmentRequest.getInitTotalAggregatedIncome());
            requestDTO.setFullAssessmentNotes(updateMeansAssessmentRequest.getFullAssessmentNotes());
            requestDTO.setFinancialAssessmentId(updateMeansAssessmentRequest.getFinancialAssessmentId());
        }

        return requestDTO;
    }
}