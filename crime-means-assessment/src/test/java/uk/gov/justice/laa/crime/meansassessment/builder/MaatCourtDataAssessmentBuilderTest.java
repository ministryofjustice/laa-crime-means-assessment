package uk.gov.justice.laa.crime.meansassessment.builder;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MaatCourtDataAssessmentBuilderTest {

    private final MaatCourtDataAssessmentBuilder requestDTOBuilder =
            new MaatCourtDataAssessmentBuilder();

    MeansAssessmentDTO assessmentDTO = TestModelDataBuilder.getMeansAssessmentDTO();

    private void checkCommonFields(MaatApiAssessmentRequest resultDto) {
        SoftAssertions.assertSoftly(softly -> {
            assertThat(resultDto.getRepId())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getRepId());
            assertThat(resultDto.getCmuId())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getCmuId());
            assertThat(resultDto.getInitNotes())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getInitAssessmentNotes());
            assertThat(resultDto.getAssessmentType())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getAssessmentType().getType());
            assertThat(resultDto.getFassInitStatus())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getAssessmentStatus().getStatus());
            assertThat(resultDto.getInitialAscrId())
                    .isEqualTo(assessmentDTO.getAssessmentCriteria().getId());
            assertThat(resultDto.getInitialAssessmentDate())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getInitialAssessmentDate());
            assertThat(resultDto.getInitOtherBenefitNote())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getOtherBenefitNote());
            assertThat(resultDto.getInitOtherIncomeNote())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getOtherIncomeNote());
            assertThat(resultDto.getInitTotAggregatedIncome())
                    .isEqualTo(assessmentDTO.getTotalAggregatedIncome());
            assertThat(resultDto.getInitAdjustedIncomeValue())
                    .isEqualTo(assessmentDTO.getAdjustedIncomeValue());
            assertThat(resultDto.getInitResult())
                    .isEqualTo(assessmentDTO.getInitAssessmentResult().getResult());
            assertThat(resultDto.getInitResultReason())
                    .isEqualTo(assessmentDTO.getInitAssessmentResult().getReason());
            assertThat(resultDto.getIncomeEvidenceDueDate())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getEvidenceDueDate());
            assertThat(resultDto.getIncomeEvidenceNotes())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getIncomeEvidenceNotes());
            assertThat(resultDto.getInitApplicationEmploymentStatus())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getEmploymentStatus());
            assertThat(resultDto.getAssessmentDetails())
                    .isEqualTo(
                            assessmentDTO.getMeansAssessment().getSectionSummaries()
                                    .stream()
                                    .flatMap(section -> section.getAssessmentDetails().stream())
                                    .collect(Collectors.toList())
                    );
            assertThat(resultDto.getChildWeightings())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getChildWeightings());
        });
    }

    @Test
    public void givenCreateRequestType_whenBuildAssessmentRequestIsInvoked_thenCreateFieldsArePopulated() {
        MaatApiAssessmentRequest resultDto =
                requestDTOBuilder.build(assessmentDTO, AssessmentRequestType.CREATE);

        checkCommonFields(resultDto);

        Consumer<MaatApiCreateAssessment> createRequirements = createRequest -> {
            assertThat(createRequest.getUsn())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getUsn());
            assertThat(createRequest.getRtCode())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getReviewType().getCode());
            assertThat(createRequest.getNworCode())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getNewWorkReason().getCode());
            assertThat(createRequest.getUserCreated())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getUserSession().getUserName());
            assertThat(createRequest.getIncomeUpliftRemoveDate())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getUpliftRemovedDate());
            assertThat(createRequest.getIncomeUpliftApplyDate())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getIncomeEvidenceSummary().getUpliftAppliedDate());
        };

        assertThat(resultDto).isInstanceOfSatisfying(MaatApiCreateAssessment.class, createRequirements);
    }

    @Test
    public void givenUpdateRequestType_whenBuildAssessmentRequestIsInvoked_thenUpdateFieldsArePopulated() {
        MaatApiAssessmentRequest resultDto =
                requestDTOBuilder.build(assessmentDTO, AssessmentRequestType.UPDATE);

        checkCommonFields(resultDto);

        Consumer<MaatApiUpdateAssessment> updateRequirements = updateRequest -> {
            assertThat(updateRequest.getFullAscrId())
                    .isEqualTo(assessmentDTO.getAssessmentCriteria().getId());
            assertThat(updateRequest.getFullAssessmentDate())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getFullAssessmentDate());
            assertThat(updateRequest.getFullResult())
                    .isEqualTo(assessmentDTO.getFullAssessmentResult().getResult());
            assertThat(updateRequest.getFullResultReason())
                    .isEqualTo(assessmentDTO.getFullAssessmentResult().getReason());
            assertThat(updateRequest.getFullAssessmentNotes())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getFullAssessmentNotes());
            assertThat(updateRequest.getFullAdjustedLivingAllowance())
                    .isEqualTo(assessmentDTO.getAdjustedLivingAllowance());
            assertThat(updateRequest.getFullTotalAnnualDisposableIncome())
                    .isEqualTo(assessmentDTO.getTotalAnnualDisposableIncome());
            assertThat(updateRequest.getFullOtherHousingNote())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getOtherHousingNote());
            assertThat(updateRequest.getFullTotalAggregatedExpenses())
                    .isEqualTo(assessmentDTO.getTotalAggregatedExpense());
            assertThat(updateRequest.getUserModified())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getUserSession().getUserName());
        };

        assertThat(resultDto).isInstanceOfSatisfying(MaatApiUpdateAssessment.class, updateRequirements);
    }
}
