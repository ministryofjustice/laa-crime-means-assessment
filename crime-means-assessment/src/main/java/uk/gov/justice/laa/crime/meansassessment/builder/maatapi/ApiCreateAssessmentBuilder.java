package uk.gov.justice.laa.crime.meansassessment.builder.maatapi;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ApiCreateAssessmentBuilder {

    public static MaatApiAssessmentRequest build(MeansAssessmentDTO assessmentDTO) {

        CurrentStatus assessmentStatus = assessmentDTO.getCurrentStatus();
        ApiCreateMeansAssessmentRequest meansAssessment = assessmentDTO.getMeansAssessment();
        AssessmentType assessmentType = meansAssessment.getAssessmentType();
        AssessmentCriteriaEntity assessmentCriteria = assessmentDTO.getAssessmentCriteria();

        return new MaatApiCreateAssessment()
                .withRepId(meansAssessment.getRepId())
                .withCmuId(meansAssessment.getCmuId())
                .withUsn(meansAssessment.getUsn())
                .withInitNotes(meansAssessment.getNotes())
                .withAssessmentType(assessmentType.getType())
                .withRtCode(meansAssessment.getReviewType().getCode())
                .withNworCode(meansAssessment.getNewWorkReason().getCode())
                .withFassInitStatus(assessmentStatus.getStatus())
                .withInitialAscrId(assessmentCriteria.getId())
                .withInitialAssessmentDate(meansAssessment.getAssessmentDate())
                .withInitOtherBenefitNote(meansAssessment.getOtherBenefitNote())
                .withInitOtherIncomeNote(meansAssessment.getOtherIncomeNote())
                .withInitTotAggregatedIncome(assessmentDTO.getTotalAggregatedIncome())
                .withInitAdjustedIncomeValue(assessmentDTO.getAdjustedIncomeValue())
                .withInitResult(assessmentDTO.getInitialAssessmentResult().getResult())
                .withInitResultReason(assessmentDTO.getInitialAssessmentResult().getReason())
                .withIncomeEvidenceDueDate(meansAssessment.getIncomeEvidenceSummary().getEvidenceDueDate())
                .withIncomeEvidenceNotes(meansAssessment.getIncomeEvidenceSummary().getIncomeEvidenceNotes())
                .withInitApplicationEmploymentStatus(meansAssessment.getEmploymentStatus())
                .withUserCreated(meansAssessment.getUserId())
                .withIncomeUpliftRemoveDate(meansAssessment.getIncomeEvidenceSummary().getUpliftRemovedDate())
                .withIncomeUpliftApplyDate(meansAssessment.getIncomeEvidenceSummary().getUpliftAppliedDate())
                .withAssessmentDetailsList(
                        meansAssessment.getSectionSummaries().stream()
                                .flatMap(section -> section.getAssessmentDetails().stream())
                                .collect(Collectors.toList())
                )
                .withChildWeightingsList(
                        meansAssessment.getChildWeightings()
                );
    }
}
