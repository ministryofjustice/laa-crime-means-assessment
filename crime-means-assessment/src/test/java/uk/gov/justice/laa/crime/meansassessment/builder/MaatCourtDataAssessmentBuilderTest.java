package uk.gov.justice.laa.crime.meansassessment.builder;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.FinancialAssessmentIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiCreateAssessment;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MaatCourtDataAssessmentBuilderTest {

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
            assertThat(resultDto.getDateCompleted())
                    .isEqualTo(assessmentDTO.getDateCompleted());
        });
    }

    private void checkCreateFields(MaatApiAssessmentRequest resultDto) {
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

    private void checkUpdateFields(MaatApiAssessmentRequest resultDto) {
        Consumer<MaatApiUpdateAssessment> updateRequirements;
        if (AssessmentType.FULL.equals(AssessmentType.getFrom(resultDto.getAssessmentType()))) {
            updateRequirements = updateRequest -> {
                assertThat(updateRequest.getFullAscrId())
                        .isEqualTo(assessmentDTO.getAssessmentCriteria().getId());
                assertThat(updateRequest.getFullAssessmentDate())
                        .isEqualTo(assessmentDTO.getMeansAssessment().getFullAssessmentDate());
                assertThat(updateRequest.getFassFullStatus())
                        .isEqualTo(assessmentDTO.getCurrentStatus().getStatus());
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
                checkEvidenceFields(updateRequest.getFinAssIncomeEvidences().get(0),
                        assessmentDTO.getMeansAssessment().getIncomeEvidence().get(0));
            };
        } else {
            updateRequirements = updateRequest -> assertThat(updateRequest.getUserModified())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getUserSession().getUserName());
        }
        assertThat(resultDto).isInstanceOfSatisfying(MaatApiUpdateAssessment.class, updateRequirements);
    }

    private void checkEvidenceFields(FinancialAssessmentIncomeEvidence finAssIncomeEvidence, ApiIncomeEvidence incomeEvidence) {
        SoftAssertions.assertSoftly(softly -> {
            assertThat(finAssIncomeEvidence.getId())
                    .isEqualTo(incomeEvidence.getId());
            assertThat(finAssIncomeEvidence.getDateModified())
                    .isEqualTo(incomeEvidence.getDateModified());
            assertThat(finAssIncomeEvidence.getDateReceived())
                    .isEqualTo(incomeEvidence.getDateReceived());
            assertThat(finAssIncomeEvidence.getActive())
                    .isEqualTo(incomeEvidence.getActive());
            assertThat(finAssIncomeEvidence.getAdhoc())
                    .isEqualTo(incomeEvidence.getAdhoc());
            assertThat(finAssIncomeEvidence.getApplicant())
                    .isEqualTo(incomeEvidence.getApplicantId());
            assertThat(finAssIncomeEvidence.getMandatory())
                    .isEqualTo(incomeEvidence.getMandatory());
            assertThat(finAssIncomeEvidence.getOtherText())
                    .isEqualTo(incomeEvidence.getOtherText());
            assertThat(finAssIncomeEvidence.getIncomeEvidence())
                    .isEqualTo(incomeEvidence.getApiEvidenceType().getCode());
            assertThat(finAssIncomeEvidence.getUserCreated())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getUserSession().getUserName());
            assertThat(finAssIncomeEvidence.getUserModified())
                    .isEqualTo(assessmentDTO.getMeansAssessment().getUserSession().getUserName());
        });
    }

    @Test
    void givenCreateRequestType_whenBuildAssessmentRequestIsInvoked_thenCreateFieldsArePopulated() {
        MaatApiAssessmentRequest resultDto =
                requestDTOBuilder.build(assessmentDTO, RequestType.CREATE);

        checkCommonFields(resultDto);
        checkCreateFields(resultDto);

        assertThat(resultDto.getFassInitStatus())
                .isEqualTo(assessmentDTO.getCurrentStatus().getStatus());
        assertThat(resultDto.getChildWeightings())
                .isEqualTo(assessmentDTO.getMeansAssessment().getChildWeightings());
    }

    @Test
    void givenUpdateRequestType_whenBuildAssessmentRequestIsInvoked_thenUpdateFieldsArePopulated() {
        MaatApiAssessmentRequest resultDto =
                requestDTOBuilder.build(assessmentDTO, RequestType.UPDATE);

        checkCommonFields(resultDto);
        checkUpdateFields(resultDto);

        assertThat(resultDto.getFassInitStatus())
                .isEqualTo(assessmentDTO.getCurrentStatus().getStatus());
        assertThat(resultDto.getChildWeightings())
                .isEqualTo(assessmentDTO.getMeansAssessment().getChildWeightings());
    }

    @Test
    void givenUpdateRequestType_whenBuildFullAssessmentRequestIsInvoked_thenChildWeightingsAreNotArePopulated() {
        assessmentDTO.getMeansAssessment().setAssessmentType(AssessmentType.FULL);
        MaatApiAssessmentRequest resultDto =
                requestDTOBuilder.build(assessmentDTO, RequestType.UPDATE);

        checkCommonFields(resultDto);
        checkUpdateFields(resultDto);
        assertThat(resultDto.getChildWeightings())
                .isNotEqualTo(assessmentDTO.getMeansAssessment().getChildWeightings());
    }
}
