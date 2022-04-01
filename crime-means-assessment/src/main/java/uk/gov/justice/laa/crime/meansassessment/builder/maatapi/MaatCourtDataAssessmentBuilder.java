package uk.gov.justice.laa.crime.meansassessment.builder.maatapi;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MaatCourtDataAssessmentBuilder {

    public MaatApiAssessmentRequest buildAssessmentRequest(final MeansAssessmentDTO assessment, final AssessmentRequestType requestType) {

        CurrentStatus assessmentStatus = assessment.getCurrentStatus();
        MeansAssessmentRequestDTO requestDTO = assessment.getMeansAssessment();
        AssessmentType assessmentType = requestDTO.getAssessmentType();
        AssessmentCriteriaEntity assessmentCriteria = assessment.getAssessmentCriteria();

        MaatApiAssessmentRequest apiAssessmentRequest;

        if (requestType.equals(AssessmentRequestType.UPDATE)) {
            apiAssessmentRequest = buildUpdate(assessment);
        } else {
            apiAssessmentRequest = buildCreate(requestDTO);
        }

        return apiAssessmentRequest
                .withRepId(requestDTO.getRepId())
                .withCmuId(requestDTO.getCmuId())
                .withInitNotes(requestDTO.getInitAssessmentNotes())
                .withAssessmentType(assessmentType.getType())
                .withFassInitStatus(assessmentStatus.getStatus())
                .withInitialAscrId(assessmentCriteria.getId())
                .withInitialAssessmentDate(requestDTO.getInitialAssessmentDate())
                .withInitOtherBenefitNote(requestDTO.getOtherBenefitNote())
                .withInitOtherIncomeNote(requestDTO.getOtherIncomeNote())
                .withInitTotAggregatedIncome(assessment.getTotalAggregatedIncome())
                .withInitAdjustedIncomeValue(assessment.getAdjustedIncomeValue())
                .withInitResult(assessment.getInitAssessmentResult().getResult())
                .withInitResultReason(assessment.getInitAssessmentResult().getReason())
                .withIncomeEvidenceDueDate(requestDTO.getIncomeEvidenceSummary().getEvidenceDueDate())
                .withIncomeEvidenceNotes(requestDTO.getIncomeEvidenceSummary().getIncomeEvidenceNotes())
                .withInitApplicationEmploymentStatus(requestDTO.getEmploymentStatus())
                .withAssessmentDetailsList(
                        requestDTO.getSectionSummaries().stream()
                                .flatMap(section -> section.getAssessmentDetails().stream())
                                .collect(Collectors.toList())
                )
                .withChildWeightingsList(
                        requestDTO.getChildWeightings()
                );

    }

    private MaatApiCreateAssessment buildCreate(MeansAssessmentRequestDTO requestDTO) {
        return new MaatApiCreateAssessment()
                .withUsn(requestDTO.getUsn())
                .withRtCode(requestDTO.getReviewType().getCode())
                .withNworCode(requestDTO.getNewWorkReason().getCode())
                .withUserCreated(requestDTO.getUserId())
                .withIncomeUpliftRemoveDate(requestDTO.getIncomeEvidenceSummary().getUpliftRemovedDate())
                .withIncomeUpliftApplyDate(requestDTO.getIncomeEvidenceSummary().getUpliftAppliedDate());
    }

    private MaatApiUpdateAssessment buildUpdate(MeansAssessmentDTO assessment) {
        MeansAssessmentRequestDTO meansAssessment = assessment.getMeansAssessment();
        return new MaatApiUpdateAssessment()
                .withFullAscrId(assessment.getAssessmentCriteria().getId())
                .withFullAssessmentDate(meansAssessment.getFullAssessmentDate())
                .withFullResult(assessment.getFullAssessmentResult().getResult())
                .withFullResultReason(assessment.getFullAssessmentResult().getReason())
                .withFullAssessmentNotes(meansAssessment.getFullAssessmentNotes())
                .withFullAdjustedLivingAllowance(assessment.getAdjustedLivingAllowance())
                .withFullTotalAnnualDisposableIncome(assessment.getTotalAnnualDisposableIncome())
                .withFullOtherHousingNote(meansAssessment.getOtherHousingNote())
                .withFullTotalAggregatedExpenses(assessment.getTotalAggregatedExpense())
                .withUserModified(meansAssessment.getUserId());
    }
}
