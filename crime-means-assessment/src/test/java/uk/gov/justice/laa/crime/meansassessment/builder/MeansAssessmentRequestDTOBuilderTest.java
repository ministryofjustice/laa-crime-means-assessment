package uk.gov.justice.laa.crime.meansassessment.builder;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MeansAssessmentRequestDTOBuilderTest {

    private final MeansAssessmentRequestDTOBuilder requestDTOBuilder =
            new MeansAssessmentRequestDTOBuilder();

    @Test
    void givenMeansAssessmentRequest_whenBuildRequestDTOisInvoked_thenCommonFieldsArePopulated() {
        ApiMeansAssessmentRequest meansAssessment =
                TestModelDataBuilder.getApiMeansAssessmentRequest(true);

        MeansAssessmentRequestDTO resultDto = requestDTOBuilder.buildRequestDTO(meansAssessment);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(resultDto.getRepId()).isEqualTo(meansAssessment.getRepId());
            assertThat(resultDto.getCmuId()).isEqualTo(meansAssessment.getCmuId());
            assertThat(resultDto.getInitialAssessmentDate()).isEqualTo(meansAssessment.getInitialAssessmentDate());
            assertThat(resultDto.getOtherBenefitNote()).isEqualTo(meansAssessment.getOtherBenefitNote());
            assertThat(resultDto.getOtherIncomeNote()).isEqualTo(meansAssessment.getOtherIncomeNote());
            assertThat(resultDto.getEmploymentStatus()).isEqualTo(meansAssessment.getEmploymentStatus());
            assertThat(resultDto.getInitAssessmentNotes()).isEqualTo(meansAssessment.getInitAssessmentNotes());
            assertThat(resultDto.getAssessmentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(resultDto.getSectionSummaries()).isEqualTo(meansAssessment.getSectionSummaries());
            assertThat(resultDto.getChildWeightings()).isEqualTo(meansAssessment.getChildWeightings());
            assertThat(resultDto.getHasPartner()).isEqualTo(meansAssessment.getHasPartner());
            assertThat(resultDto.getPartnerContraryInterest()).isEqualTo(meansAssessment.getPartnerContraryInterest());
            assertThat(resultDto.getAssessmentType()).isEqualTo(meansAssessment.getAssessmentType());
            assertThat(resultDto.getCaseType()).isEqualTo(meansAssessment.getCaseType());
            assertThat(resultDto.getUserSession()).isEqualTo(meansAssessment.getUserSession());
            assertThat(resultDto.getIncomeEvidenceSummary()).isEqualTo(meansAssessment.getIncomeEvidenceSummary());
            assertThat(resultDto.getCrownCourtOverview()).isEqualTo(meansAssessment.getCrownCourtOverview());
            assertThat(resultDto.getMagCourtOutcome()).isEqualTo(meansAssessment.getMagCourtOutcome());
        });
    }

    @Test
    void givenInitMeansAssessmentRequest_whenBuildRequestDTOIsInvoked_thenInitFieldsArePopulated() {
        ApiCreateMeansAssessmentRequest createMeansAssessment =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(true);

        MeansAssessmentRequestDTO resultDto = requestDTOBuilder.buildRequestDTO(createMeansAssessment);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(resultDto.getUsn()).isEqualTo(createMeansAssessment.getUsn());
            assertThat(resultDto.getReviewType()).isEqualTo(createMeansAssessment.getReviewType());
            assertThat(resultDto.getNewWorkReason()).isEqualTo(createMeansAssessment.getNewWorkReason());
        });
    }

    @Test
    void givenFullMeansAssessmentRequest_whenBuildRequestDTOIsInvoked_thenFullFieldsArePopulated() {
        ApiUpdateMeansAssessmentRequest updateMeansAssessment =
                TestModelDataBuilder.getApiUpdateMeansAssessmentRequest(true);

        MeansAssessmentRequestDTO resultDto = requestDTOBuilder.buildRequestDTO(updateMeansAssessment);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(resultDto.getFullAssessmentDate()).isEqualTo(updateMeansAssessment.getFullAssessmentDate());
            assertThat(resultDto.getOtherHousingNote()).isEqualTo(updateMeansAssessment.getOtherHousingNote());
            assertThat(resultDto.getInitTotalAggregatedIncome()).isEqualTo(updateMeansAssessment.getInitTotalAggregatedIncome());
            assertThat(resultDto.getFullAssessmentNotes()).isEqualTo(updateMeansAssessment.getFullAssessmentNotes());
        });
    }
}
