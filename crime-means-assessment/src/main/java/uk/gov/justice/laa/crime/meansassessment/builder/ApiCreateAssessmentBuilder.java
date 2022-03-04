package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitialAssessmentResult;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ApiCreateAssessmentBuilder {

    public ApiCreateAssessment build(BigDecimal annualTotal, CurrentStatus status, BigDecimal adjustedIncome,
                                     InitialAssessmentResult result, AssessmentCriteriaEntity assessmentCriteria, ApiCreateMeansAssessmentRequest meansAssessment) {
        return new ApiCreateAssessment().withRepId(meansAssessment.getRepId())
                .withCmuId(meansAssessment.getCmuId())
                .withInitialAscrId(assessmentCriteria.getId())
                .withCmuId(meansAssessment.getCmuId())
                .withFassInitStatus(status.getStatus())
                .withInitialAssessmentDate(meansAssessment.getAssessmentDate())
                .withInitOtherBenefitNote(meansAssessment.getOtherBenefitNote())
                .withInitOtherIncomeNote(meansAssessment.getOtherIncomeNote())
                .withInitTotAggregatedIncome(annualTotal)
                .withInitAdjustedIncomeValue(adjustedIncome)
                .withInitNotes(meansAssessment.getNotes())
                .withInitResult(result.getResult())
                .withInitResultReason(result.getReason())
                .withIncomeEvidenceDueDate(meansAssessment.getIncomeEvidenceSummary().getEvidenceDueDate())
                .withIncomeEvidenceNotes(meansAssessment.getIncomeEvidenceSummary().getIncomeEvidenceNotes())
                .withInitApplicationEmploymentStatus(meansAssessment.getEmploymentStatus())
                .withUsn(meansAssessment.getUsn())
                .withRtCode(meansAssessment.getReviewType().getCode())
                .withNworCode(meansAssessment.getNewWorkReason().getCode())
                .withUserCreated(meansAssessment.getUserId())
                .withIncomeUpliftRemoveDate(meansAssessment.getIncomeEvidenceSummary().getUpliftRemovedDate())
                .withIncomeUpliftApplyDate(meansAssessment.getIncomeEvidenceSummary().getUpliftAppliedDate())
                .withAssessmentDetailsList(
                        meansAssessment.getSectionSummaries().stream()
                                .flatMap(section -> section.getAssessmentDetails().stream())
                                .collect(Collectors.toList())
                );
    }
}
