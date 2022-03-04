package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.InitialMeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitialAssessmentResult;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CreateInitialAssessmentBuilder {

    public ApiCreateAssessment build(InitialMeansAssessmentDTO initialMeansAssessmentDTO) {

        ApiCreateAssessment apiCreateAssessment = new ApiCreateAssessment();

        if (initialMeansAssessmentDTO.getAssessmentResult() == InitialAssessmentResult.NONE) {
            apiCreateAssessment.withInitResult(null).withInitResultReason(null);
        } else {
            apiCreateAssessment.withInitResult(initialMeansAssessmentDTO.getAssessmentResult().getResult())
            .withInitResultReason(initialMeansAssessmentDTO.getAssessmentResult().getReason());
        }

        return apiCreateAssessment.withRepId(initialMeansAssessmentDTO.getMeansAssessment().getRepId())
                .withCmuId(initialMeansAssessmentDTO.getMeansAssessment().getCmuId())
                .withInitialAscrId(initialMeansAssessmentDTO.getAssessmentCriteria().getId())
                .withCmuId(initialMeansAssessmentDTO.getMeansAssessment().getCmuId())
                .withFassInitStatus(initialMeansAssessmentDTO.getInitStatus().getStatus())
                .withInitialAssessmentDate(initialMeansAssessmentDTO.getMeansAssessment().getInitialAssessmentDate())
                .withInitOtherBenefitNote(initialMeansAssessmentDTO.getMeansAssessment().getOtherBenefitNote())
                .withInitOtherIncomeNote(initialMeansAssessmentDTO.getMeansAssessment().getOtherIncomeNote())
                .withInitTotAggregatedIncome(initialMeansAssessmentDTO.getAnnualTotal())
                .withInitAdjustedIncomeValue(initialMeansAssessmentDTO.getAdjustedIncomeValue())
                .withInitNotes(initialMeansAssessmentDTO.getMeansAssessment().getNotes())
                .withIncomeEvidenceDueDate(initialMeansAssessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getEvidenceDueDate())
                .withIncomeEvidenceNotes(initialMeansAssessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getIncomeEvidenceNotes())
                .withInitApplicationEmploymentStatus(initialMeansAssessmentDTO.getMeansAssessment().getEmploymentStatus())
                .withUsn(initialMeansAssessmentDTO.getMeansAssessment().getUsn())
                .withRtCode(initialMeansAssessmentDTO.getMeansAssessment().getReviewType().getCode())
                .withNworCode(initialMeansAssessmentDTO.getMeansAssessment().getNewWorkReason().getCode())
                .withUserCreated(initialMeansAssessmentDTO.getMeansAssessment().getUserId())
                .withIncomeUpliftRemoveDate(initialMeansAssessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getUpliftRemovedDate())
                .withIncomeUpliftApplyDate(initialMeansAssessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getUpliftAppliedDate())
                .withAssessmentDetailsList(
                        initialMeansAssessmentDTO.getSectionSummaries().stream()
                                .flatMap(section -> section.getAssessmentDetails().stream())
                                .collect(Collectors.toList())
                );
    }
}
