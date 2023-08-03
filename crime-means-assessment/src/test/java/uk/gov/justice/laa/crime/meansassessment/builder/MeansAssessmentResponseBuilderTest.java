package uk.gov.justice.laa.crime.meansassessment.builder;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

 class MeansAssessmentResponseBuilderTest {

    private final MeansAssessmentResponseBuilder responseBuilder =
            new MeansAssessmentResponseBuilder();
    private final AssessmentCriteriaEntity assessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntity();
    private MeansAssessmentDTO completedAssessment;
    private MaatApiAssessmentResponse maatApiAssessmentResponse;

    @BeforeEach
     void setup() {
        completedAssessment = TestModelDataBuilder.getMeansAssessmentDTO();
        maatApiAssessmentResponse = TestModelDataBuilder.getMaatApiInitAssessmentResponse();
    }

    private void checkCommonFieldsPopulated(ApiMeansAssessmentResponse response) {
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.getAssessmentId())
                    .isEqualTo(maatApiAssessmentResponse.getId());
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
                    .isEqualTo(maatApiAssessmentResponse.getInitResult());
            assertThat(response.getInitResultReason())
                    .isEqualTo(maatApiAssessmentResponse.getInitResultReason());
            assertThat(response.getAdjustedIncomeValue())
                    .isEqualTo(completedAssessment.getAdjustedIncomeValue());
            assertThat(response.getAssessmentSectionSummary())
                    .isEqualTo(completedAssessment.getMeansAssessment().getSectionSummaries());
        });
    }

    @Test
     void givenInitAssessmentType_whenBuildIsInvoked_thenCommonFieldsArePopulated() {
        ApiMeansAssessmentResponse response =
                responseBuilder.build(maatApiAssessmentResponse, assessmentCriteria, completedAssessment);
        checkCommonFieldsPopulated(response);
        assertThat(response.getChildWeightings())
                .isEqualTo(completedAssessment.getMeansAssessment().getChildWeightings());
    }

    @Test
     void givenFullAssessmentType_whenBuildIsInvoked_thenFullFieldsArePopulated() {
        completedAssessment.getMeansAssessment().setAssessmentType(AssessmentType.FULL);
        ApiMeansAssessmentResponse response =
                responseBuilder.build(maatApiAssessmentResponse, assessmentCriteria, completedAssessment);

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
            assertThat(response.getChildWeightings())
                    .isNotEqualTo(completedAssessment.getMeansAssessment().getChildWeightings());
        });

    }
}
