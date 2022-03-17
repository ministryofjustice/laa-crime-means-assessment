package uk.gov.justice.laa.crime.meansassessment.builder.maatapi;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MaatAPIAssessmentBuilder {

    public MaatApiAssessmentRequest build(MeansAssessmentDTO assessment, AssessmentRequestType requestType) {

        CurrentStatus assessmentStatus = assessment.getCurrentStatus();
        ApiCreateMeansAssessmentRequest meansAssessment = assessment.getMeansAssessment();
        AssessmentType assessmentType = meansAssessment.getAssessmentType();
        AssessmentCriteriaEntity assessmentCriteria = assessment.getAssessmentCriteria();

        MaatApiAssessmentRequest apiAssessmentRequest;

        if (requestType.equals(AssessmentRequestType.UPDATE)) {
            apiAssessmentRequest = buildUpdate(assessment);
        } else {
            apiAssessmentRequest = buildCreate(meansAssessment);
        }

        return apiAssessmentRequest
                .withRepId(meansAssessment.getRepId())
                .withCmuId(meansAssessment.getCmuId())
                .withInitNotes(meansAssessment.getNotes())
                .withAssessmentType(assessmentType.getType())
                .withFassInitStatus(assessmentStatus.getStatus())
                .withInitialAscrId(assessmentCriteria.getId())
                .withInitialAssessmentDate(meansAssessment.getAssessmentDate())
                .withInitOtherBenefitNote(meansAssessment.getOtherBenefitNote())
                .withInitOtherIncomeNote(meansAssessment.getOtherIncomeNote())
                .withInitTotAggregatedIncome(assessment.getTotalAggregatedIncome())
                .withInitAdjustedIncomeValue(assessment.getAdjustedIncomeValue())
                .withInitResult(assessment.getInitialAssessmentResult().getResult())
                .withInitResultReason(assessment.getInitialAssessmentResult().getReason())
                .withIncomeEvidenceDueDate(meansAssessment.getIncomeEvidenceSummary().getEvidenceDueDate())
                .withIncomeEvidenceNotes(meansAssessment.getIncomeEvidenceSummary().getIncomeEvidenceNotes())
                .withInitApplicationEmploymentStatus(meansAssessment.getEmploymentStatus())
                .withAssessmentDetailsList(
                        meansAssessment.getSectionSummaries().stream()
                                .flatMap(section -> section.getAssessmentDetails().stream())
                                .collect(Collectors.toList())
                )
                .withChildWeightingsList(
                        meansAssessment.getChildWeightings()
                );

    }

    private MaatApiCreateAssessment buildCreate(ApiCreateMeansAssessmentRequest meansAssessment) {
        return new MaatApiCreateAssessment()
                .withUsn(meansAssessment.getUsn())
                .withRtCode(meansAssessment.getReviewType().getCode())
                .withNworCode(meansAssessment.getNewWorkReason().getCode())
                .withUserCreated(meansAssessment.getUserId())
                .withIncomeUpliftRemoveDate(meansAssessment.getIncomeEvidenceSummary().getUpliftRemovedDate())
                .withIncomeUpliftApplyDate(meansAssessment.getIncomeEvidenceSummary().getUpliftAppliedDate());
    }

    public MaatApiUpdateAssessment buildUpdate(MeansAssessmentDTO assessment) {
        ApiCreateMeansAssessmentRequest meansAssessment = assessment.getMeansAssessment();
        return new MaatApiUpdateAssessment()
                .withFullAscrId(assessment.getAssessmentCriteria().getId())
                .withFullAssessmentDate(meansAssessment.getAssessmentDate())
                .withFullResult(assessment.getFullAssessmentResult().getResult())
                .withFullResultReason(assessment.getFullAssessmentResult().getReason())
                .withFullAssessmentNotes(meansAssessment.getNotes())
                .withFullAdjustedLivingAllowance(assessment.getAdjustedLivingAllowance())
                .withFullTotalAnnualDisposableIncome(assessment.getTotalAnnualDisposableIncome())
                .withFullOtherHousingNote(meansAssessment.getOtherHousingNote())
                .withFullTotalAggregatedExpenses(assessment.getTotalAggregatedExpense())
                .withUserModified(meansAssessment.getUserId());
    }
}