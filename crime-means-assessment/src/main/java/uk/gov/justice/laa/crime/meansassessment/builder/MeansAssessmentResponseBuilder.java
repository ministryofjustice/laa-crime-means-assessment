package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

@Slf4j
@Component
@AllArgsConstructor
public class MeansAssessmentResponseBuilder {

    public ApiMeansAssessmentResponse build(final Integer financialAssessmentID,
                                            final AssessmentCriteriaEntity assessmentCriteria,
                                            final MeansAssessmentDTO completedAssessment) {

        ApiMeansAssessmentResponse response = new ApiMeansAssessmentResponse()
                .withAssessmentId(financialAssessmentID)
                .withRepId(completedAssessment.getMeansAssessment().getRepId())
                .withCriteriaId(assessmentCriteria.getId())
                .withLowerThreshold(assessmentCriteria.getInitialLowerThreshold())
                .withUpperThreshold(assessmentCriteria.getInitialUpperThreshold())
                .withTotalAggregatedIncome(completedAssessment.getTotalAggregatedIncome())
                .withInitResult(completedAssessment.getInitAssessmentResult().getResult())
                .withInitResultReason(completedAssessment.getInitAssessmentResult().getReason())
                .withAdjustedIncomeValue(completedAssessment.getAdjustedIncomeValue())
                .withAssessmentSectionSummary(completedAssessment.getMeansAssessment().getSectionSummaries());

        AssessmentType assessmentType = completedAssessment.getMeansAssessment().getAssessmentType();
        if (AssessmentType.FULL.equals(assessmentType)) {
            buildFull(assessmentCriteria, completedAssessment, response);
        }
        return response;
    }

    void buildFull(AssessmentCriteriaEntity assessmentCriteria, MeansAssessmentDTO completedAssessment,
                   ApiMeansAssessmentResponse response) {
        response.withAdjustedLivingAllowance(completedAssessment.getAdjustedLivingAllowance())
                .withTotalAnnualDisposableIncome(completedAssessment.getTotalAnnualDisposableIncome())
                .withFullThreshold(assessmentCriteria.getFullThreshold())
                .withTotalAggregatedExpense(completedAssessment.getTotalAggregatedExpense())
                .withFullResult(completedAssessment.getFullAssessmentResult().getResult())
                .withFullResultReason(completedAssessment.getFullAssessmentResult().getReason());
    }
}
