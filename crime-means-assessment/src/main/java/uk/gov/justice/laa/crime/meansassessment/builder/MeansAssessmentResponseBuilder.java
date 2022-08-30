package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;

import static java.util.Optional.ofNullable;

@Slf4j
@Component
@AllArgsConstructor
public class MeansAssessmentResponseBuilder {

    public ApiMeansAssessmentResponse build(final MaatApiAssessmentResponse maatApiAssessmentResponse,
                                            final AssessmentCriteriaEntity assessmentCriteria,
                                            final MeansAssessmentDTO completedAssessment) {

        ApiMeansAssessmentResponse response = new ApiMeansAssessmentResponse()
                .withAssessmentId(maatApiAssessmentResponse.getId())
                .withRepId(completedAssessment.getMeansAssessment().getRepId())
                .withCriteriaId(assessmentCriteria.getId())
                .withLowerThreshold(assessmentCriteria.getInitialLowerThreshold())
                .withUpperThreshold(assessmentCriteria.getInitialUpperThreshold())
                .withTotalAggregatedIncome(completedAssessment.getTotalAggregatedIncome())
                .withInitResult(maatApiAssessmentResponse.getInitResult())
                .withInitResultReason(maatApiAssessmentResponse.getInitResultReason())
                .withAdjustedIncomeValue(completedAssessment.getAdjustedIncomeValue())
                .withAssessmentSectionSummary(completedAssessment.getMeansAssessment().getSectionSummaries());

        AssessmentType assessmentType = completedAssessment.getMeansAssessment().getAssessmentType();
        if (AssessmentType.FULL.equals(assessmentType)) {
            buildFull(assessmentCriteria, completedAssessment, response);
        }
        if (AssessmentType.INIT.equals(assessmentType)) {
            response.withChildWeightings(maatApiAssessmentResponse.getChildWeightings());
        }

        return response;
    }

    void buildFull(AssessmentCriteriaEntity assessmentCriteria, MeansAssessmentDTO completedAssessment,
                   ApiMeansAssessmentResponse response) {
        response.withAdjustedLivingAllowance(completedAssessment.getAdjustedLivingAllowance())
                .withTotalAnnualDisposableIncome(completedAssessment.getTotalAnnualDisposableIncome())
                .withFullThreshold(assessmentCriteria.getFullThreshold())
                .withTotalAggregatedExpense(completedAssessment.getTotalAggregatedExpense())
                .withFullResult(ofNullable(completedAssessment.getFullAssessmentResult())
                        .map(FullAssessmentResult::getResult).orElse(null))
                .withFullResultReason(ofNullable(completedAssessment.getFullAssessmentResult())
                        .map(FullAssessmentResult::getReason).orElse(null));
    }
}
