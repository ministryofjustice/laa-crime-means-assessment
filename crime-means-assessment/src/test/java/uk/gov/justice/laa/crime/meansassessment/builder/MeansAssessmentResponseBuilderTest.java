package uk.gov.justice.laa.crime.meansassessment.builder;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeansAssessmentResponseBuilderTest {

    private final MeansAssessmentResponseBuilder responseBuilder =
            new MeansAssessmentResponseBuilder();

    private final MeansAssessmentDTO completedAssessment = TestModelDataBuilder.getMeansAssessmentDTO();
    private final AssessmentCriteriaEntity assessmentCriteria = TestModelDataBuilder.getAssessmentCriteriaEntity();

    private void checkCommonFieldsPopulated(ApiMeansAssessmentResponse response) {
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.getAssessmentId())
                    .isEqualTo(TestModelDataBuilder.MEANS_ASSESSMENT_ID);
            assertThat(response.getRepId())
                    .isEqualTo(completedAssessment.getMeansAssessment().getRepId());
            assertThat(response.getCriteriaId())
                    .isEqualTo(assessmentCriteria.getId());
            assertThat(response.getLowerThreshold())
                    .isEqualTo(assessmentCriteria.getInitialLowerThreshold());
            assertThat(response.getUpperThreshold())
                    .isEqualTo(assessmentCriteria.getInitialUpperThreshold());
            assertThat(response.getTotalAggregatedIncome())
                    .isEqualTo(completedAssessment.getTotalAggregatedIncome());
            assertThat(response.getInitResult())
                    .isEqualTo(completedAssessment.getInitAssessmentResult().getResult());
            assertThat(response.getInitResultReason())
                    .isEqualTo(completedAssessment.getInitAssessmentResult().getReason());
            assertThat(response.getAdjustedIncomeValue())
                    .isEqualTo(completedAssessment.getAdjustedIncomeValue());
            assertThat(response.getAssessmentSectionSummary())
                    .isEqualTo(completedAssessment.getMeansAssessment().getSectionSummaries());
        });
    }

    @Test
    public void givenInitAssessmentType_whenBuildIsInvoked_thenCommonFieldsArePopulated() {
        ApiMeansAssessmentResponse response =
                responseBuilder.build(TestModelDataBuilder.MEANS_ASSESSMENT_ID, assessmentCriteria, completedAssessment);
        checkCommonFieldsPopulated(response);
    }

    @Test
    public void givenFullAssessmentType_whenBuildIsInvoked_thenFullFieldsArePopulated() {
        completedAssessment.getMeansAssessment().setAssessmentType(AssessmentType.FULL);
        ApiMeansAssessmentResponse response =
                responseBuilder.build(TestModelDataBuilder.MEANS_ASSESSMENT_ID, assessmentCriteria, completedAssessment);

        checkCommonFieldsPopulated(response);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.getAdjustedLivingAllowance())
                    .isEqualTo(completedAssessment.getAdjustedLivingAllowance());
            assertThat(response.getTotalAnnualDisposableIncome())
                    .isEqualTo(completedAssessment.getTotalAnnualDisposableIncome());
            assertThat(response.getFullThreshold())
                    .isEqualTo(assessmentCriteria.getFullThreshold());
            assertThat(response.getTotalAggregatedExpense())
                    .isEqualTo(completedAssessment.getTotalAggregatedExpense());
            assertThat(response.getFullResult())
                    .isEqualTo(completedAssessment.getFullAssessmentResult().getResult());
            assertThat(response.getFullResultReason())
                    .isEqualTo(completedAssessment.getFullAssessmentResult().getReason());
        });

    }
}
