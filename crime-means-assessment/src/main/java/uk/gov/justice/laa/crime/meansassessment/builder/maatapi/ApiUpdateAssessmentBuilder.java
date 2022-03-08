package uk.gov.justice.laa.crime.meansassessment.builder.maatapi;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ApiUpdateAssessmentBuilder {
    public static MaatApiAssessmentRequest build(MeansAssessmentDTO assessmentDTO) {

        CurrentStatus assessmentStatus = assessmentDTO.getCurrentStatus();
        ApiCreateMeansAssessmentRequest meansAssessment = assessmentDTO.getMeansAssessment();
        AssessmentType assessmentType = meansAssessment.getAssessmentType();
        AssessmentCriteriaEntity assessmentCriteria = assessmentDTO.getAssessmentCriteria();

        return new MaatApiUpdateAssessment()
                .withRepId(meansAssessment.getRepId())
                .withCmuId(meansAssessment.getCmuId())
                .withInitNotes(meansAssessment.getNotes())
                .withAssessmentType(assessmentType.getType())
                .withFassInitStatus(assessmentStatus.getStatus())
                .withInitialAscrId(assessmentCriteria.getId())
                .withInitialAssessmentDate(meansAssessment.getAssessmentDate())
                .withInitOtherBenefitNote(meansAssessment.getOtherBenefitNote())
                .withInitOtherIncomeNote(meansAssessment.getOtherIncomeNote())
                .withInitTotAggregatedIncome(assessmentDTO.getAnnualTotal())
                .withInitAdjustedIncomeValue(assessmentDTO.getAdjustedIncomeValue())
                .withInitResult(assessmentDTO.getInitialAssessmentResult().getResult())
                .withInitResultReason(assessmentDTO.getInitialAssessmentResult().getReason())
                .withIncomeEvidenceDueDate(meansAssessment.getIncomeEvidenceSummary().getEvidenceDueDate())
                .withIncomeEvidenceNotes(meansAssessment.getIncomeEvidenceSummary().getIncomeEvidenceNotes())
                .withInitApplicationEmploymentStatus(meansAssessment.getEmploymentStatus())
                .withFullAscrId(assessmentCriteria.getId())
                .withFullAssessmentDate(meansAssessment.getAssessmentDate())
                .withFullResult(assessmentDTO.getFullAssessmentResult().getResult())
                .withFullResultReason(assessmentDTO.getFullAssessmentResult().getReason())
                .withFullAssessmentNotes(meansAssessment.getNotes())
                .withFullAdjustedLivingAllowance(assessmentDTO.getAdjustedLivingAllowance())
                .withFullTotalAnnualDisposableIncome(assessmentDTO.getTotalAnnualDisposableIncome())
                .withFullOtherHousingNote(meansAssessment.getOtherHousingNote())
                .withFullTotalAggregatedExpenses(assessmentDTO.getTotalAggregatedExpense())
                .withUserModified(meansAssessment.getUserId())
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
