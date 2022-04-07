package uk.gov.justice.laa.crime.meansassessment.builder.maatapi;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.FinAssessmentChildWeightHistoryDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.FinAssessmentDetailsHistoryDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.FinAssessmentsHistoryDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
public class FinAssessmentHistoryDTOBuilder {

    public static FinAssessmentsHistoryDTO buildFinAssessmentHistory(final MaatApiAssessmentResponse maatApiAssessmentResponse,
                                                                     final ApiCreateMeansAssessmentResponse assessmentResponse) {
        return FinAssessmentsHistoryDTO.builder()
                .initialAssessmentDate(maatApiAssessmentResponse.getInitialAssessmentDate())
                .cmuId(maatApiAssessmentResponse.getCmuId())
                .assessmentType(maatApiAssessmentResponse.getAssessmentType())
                .initialAscrId(maatApiAssessmentResponse.getInitialAscrId())
                .fullAscrId(maatApiAssessmentResponse.getFullAscrId())
//                .timeStamp(maatApiAssessmentResponse.getTimeStamp)
                .dateCreated(maatApiAssessmentResponse.getDateCreated())
                .assessmentId(maatApiAssessmentResponse.getId())
                .adjustedLivingAllowance(maatApiAssessmentResponse.getFullAdjustedLivingAllowance())
                .fullAssessmentNotes(maatApiAssessmentResponse.getFullAssessmentNotes())
                .otherHousingNote(maatApiAssessmentResponse.getFullOtherHousingNote())
                .fullResult(maatApiAssessmentResponse.getFullResult())
                .fullResultReason(maatApiAssessmentResponse.getFullResultReason())
                .totalAnnualDisposableIncome(maatApiAssessmentResponse.getFullTotalAnnualDisposableIncome())
                .fassInitStatus(maatApiAssessmentResponse.getFassInitStatus())
                .fassFullStatus(maatApiAssessmentResponse.getFassFullStatus())
                .adjustedIncomeValue(maatApiAssessmentResponse.getInitAdjustedIncomeValue())
                .initApplicationEmploymentStatus(maatApiAssessmentResponse.getInitApplicationEmploymentStatus())
//                .initAppPartner(maatApiAssessmentResponse.getInitAppPartner)
                .initAssessmentNotes(maatApiAssessmentResponse.getInitNotes())
                .otherBenefitNote(maatApiAssessmentResponse.getInitOtherBenefitNote())
                .otherIncomeNote(maatApiAssessmentResponse.getInitOtherIncomeNote())
                .initResult(maatApiAssessmentResponse.getInitResult())
                .initResultReason(maatApiAssessmentResponse.getInitResultReason())
                .initTotalAggregatedIncome(maatApiAssessmentResponse.getInitTotAggregatedIncome())
//                .incomeEvidenceRecDate(maatApiAssessmentResponse.)
//                .residentialStatus(maatApiAssessmentResponse.)
                .incomeEvidenceDueDate(maatApiAssessmentResponse.getIncomeEvidenceDueDate())
                .incomeEvidenceNotes(maatApiAssessmentResponse.getIncomeEvidenceNotes())
                .incomeUpliftApplyDate(maatApiAssessmentResponse.getIncomeUpliftApplyDate())
                .incomeUpliftRemoveDate(maatApiAssessmentResponse.getIncomeUpliftRemoveDate())
//                .incomeUpliftPercentage(maatApiAssessmentResponse.get)
//                .firstIncomeReminderDate(maatApiAssessmentResponse.)
//                .secondIncomeReminderDate(maatApiAssessmentResponse.)
                .usn(maatApiAssessmentResponse.getUsn())
//                .valid(maatApiAssessmentResponse.)
                .fullAssessmentAvailable(assessmentResponse.getFullAssessmentAvailable())
                .fullAssessmentDate(maatApiAssessmentResponse.getFullAssessmentDate())
//                .replaced(maatApiAssessmentResponse.getReplaced())
                .userCreated(maatApiAssessmentResponse.getUserCreated())
                .userModified(maatApiAssessmentResponse.getUserModified())
                .rtCode(maatApiAssessmentResponse.getRtCode())
                .dateCompleted(maatApiAssessmentResponse.getDateCompleted())
                .repId(maatApiAssessmentResponse.getRepId())
                .nworCode(maatApiAssessmentResponse.getNworCode())
                .assessmentDetailsList(buildFinAssessmentDetailsHistoryDTO(maatApiAssessmentResponse.getAssessmentDetailsList()))
                .childWeightingsList(buildFinAssessmentChildWeightHistoryDTO(maatApiAssessmentResponse.getChildWeightingsList()))
                .build();
    }

    private static List<FinAssessmentDetailsHistoryDTO> buildFinAssessmentDetailsHistoryDTO(final List<ApiAssessmentDetail> apiAssessmentDetails) {
        return ofNullable(apiAssessmentDetails)
                .orElse(Collections.emptyList()).stream()
                .map(apiAssessmentDetail -> FinAssessmentDetailsHistoryDTO.builder()
                        .applicantAmount(apiAssessmentDetail.getApplicantAmount())
                        .applicantFrequency(apiAssessmentDetail.getApplicantFrequency())
                        .partnerFrequency(apiAssessmentDetail.getPartnerFrequency())
                        .partnerAmount(apiAssessmentDetail.getPartnerAmount())
                        .criteriaDetailId(apiAssessmentDetail.getCriteriaDetailId())
//                        .finAssessmentDetailId(apiAssessmentDetail.getId())
//                        .userCreated(apiAssessmentDetail.getUserCreated)
                        .build())
                .collect(Collectors.toList());
    }

    private static List<FinAssessmentChildWeightHistoryDTO> buildFinAssessmentChildWeightHistoryDTO(final List<ApiAssessmentChildWeighting> apiAssessmentChildWeightings) {
        return ofNullable(apiAssessmentChildWeightings)
                .orElse(Collections.emptyList()).stream()
                .map(apiAssessmentChildWeighting -> FinAssessmentChildWeightHistoryDTO.builder()
                        .noOfChildren(apiAssessmentChildWeighting.getNoOfChildren())
                        .weightingId(apiAssessmentChildWeighting.getWeightingId())
//                        .finAssChildWeightingId()
//                        .userCreated()
                        .build())
                .collect(Collectors.toList());
    }
}
