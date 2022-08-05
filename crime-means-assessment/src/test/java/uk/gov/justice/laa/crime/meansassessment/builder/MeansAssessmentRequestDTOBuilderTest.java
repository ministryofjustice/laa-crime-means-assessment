package uk.gov.justice.laa.crime.meansassessment.builder;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiUpdateMeansAssessmentRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeansAssessmentRequestDTOBuilderTest {

    private final MeansAssessmentRequestDTOBuilder requestDTOBuilder =
            new MeansAssessmentRequestDTOBuilder();

    @Test
    public void givenMeansAssessmentRequest_whenBuildRequestDTOisInvoked_thenCommonFieldsArePopulated() {
        ApiMeansAssessmentRequest meansAssessment =
                TestModelDataBuilder.getApiMeansAssessmentRequest(true);

        MeansAssessmentRequestDTO resultDto = requestDTOBuilder.buildRequestDTO(meansAssessment);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(resultDto.getLaaTransactionId()).isEqualTo(meansAssessment.getLaaTransactionId());
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
    public void givenInitMeansAssessmentRequest_whenBuildRequestDTOIsInvoked_thenInitFieldsArePopulated() {
        ApiCreateMeansAssessmentRequest initMeansAssessment =
                TestModelDataBuilder.getApiCreateMeansAssessmentRequest(true);

        MeansAssessmentRequestDTO resultDto = requestDTOBuilder.buildRequestDTO(initMeansAssessment);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(resultDto.getUsn()).isEqualTo(initMeansAssessment.getUsn());
            assertThat(resultDto.getReviewType()).isEqualTo(initMeansAssessment.getReviewType());
            assertThat(resultDto.getNewWorkReason()).isEqualTo(initMeansAssessment.getNewWorkReason());
        });
    }

    @Test
    public void givenFullMeansAssessmentRequest_whenBuildRequestDTOIsInvoked_thenFullFieldsArePopulated() {
        ApiUpdateMeansAssessmentRequest fullMeansAssessment =
                TestModelDataBuilder.getApiUpdateMeansAssessmentRequest(true);

        MeansAssessmentRequestDTO resultDto = requestDTOBuilder.buildRequestDTO(fullMeansAssessment);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(resultDto.getFullAssessmentDate()).isEqualTo(fullMeansAssessment.getFullAssessmentDate());
            assertThat(resultDto.getOtherHousingNote()).isEqualTo(fullMeansAssessment.getOtherHousingNote());
            assertThat(resultDto.getInitTotalAggregatedIncome()).isEqualTo(fullMeansAssessment.getInitTotalAggregatedIncome());
            assertThat(resultDto.getFullAssessmentNotes()).isEqualTo(fullMeansAssessment.getFullAssessmentNotes());
        });
    }
}
