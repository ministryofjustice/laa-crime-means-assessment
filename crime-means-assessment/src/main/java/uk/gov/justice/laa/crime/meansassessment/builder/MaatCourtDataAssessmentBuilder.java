package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidenceSummary;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiCreateAssessment;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import static java.util.Optional.ofNullable;

@Component
@AllArgsConstructor
@Slf4j
public class MaatCourtDataAssessmentBuilder {

    public MaatApiAssessmentRequest build(final MeansAssessmentDTO assessment, final RequestType requestType) {

        CurrentStatus assessmentStatus = assessment.getCurrentStatus();
        MeansAssessmentRequestDTO requestDTO = assessment.getMeansAssessment();
        AssessmentType assessmentType = requestDTO.getAssessmentType();
        AssessmentCriteriaEntity assessmentCriteria = assessment.getAssessmentCriteria();

        MaatApiAssessmentRequest apiAssessmentRequest;

        if (RequestType.UPDATE.equals(requestType)) {
            apiAssessmentRequest = buildUpdate(assessment);
        } else {
            apiAssessmentRequest = buildCreate(requestDTO);
        }

        if (AssessmentType.INIT.equals(assessmentType)) {
            apiAssessmentRequest = apiAssessmentRequest.withChildWeightings(
                    requestDTO.getChildWeightings()
            );
            apiAssessmentRequest.withFassInitStatus(assessmentStatus.getStatus());
        }

        return apiAssessmentRequest
                .withRepId(requestDTO.getRepId())
                .withCmuId(requestDTO.getCmuId())
                .withInitNotes(requestDTO.getInitAssessmentNotes())
                .withAssessmentType(assessmentType.getType())
                .withInitialAscrId(assessmentCriteria.getId())
                .withInitialAssessmentDate(requestDTO.getInitialAssessmentDate())
                .withInitOtherBenefitNote(requestDTO.getOtherBenefitNote())
                .withInitOtherIncomeNote(requestDTO.getOtherIncomeNote())
                .withInitTotAggregatedIncome(assessment.getTotalAggregatedIncome())
                .withInitAdjustedIncomeValue(assessment.getAdjustedIncomeValue())
                .withInitResult(ofNullable(assessment.getInitAssessmentResult())
                        .map(InitAssessmentResult::getResult).orElse(null))
                .withInitResultReason(ofNullable(assessment.getInitAssessmentResult())
                        .map(InitAssessmentResult::getReason).orElse(null))
                .withIncomeEvidenceDueDate(ofNullable(requestDTO.getIncomeEvidenceSummary())
                        .map(ApiIncomeEvidenceSummary::getEvidenceDueDate).orElse(null))
                .withIncomeEvidenceNotes(ofNullable(requestDTO.getIncomeEvidenceSummary())
                        .map(ApiIncomeEvidenceSummary::getIncomeEvidenceNotes).orElse(null))
                .withInitApplicationEmploymentStatus(requestDTO.getEmploymentStatus())
                .withAssessmentDetails(
                        requestDTO.getSectionSummaries().stream()
                                .flatMap(section -> section.getAssessmentDetails().stream())
                                .toList()
                )
                .withDateCompleted(assessment.getDateCompleted());
    }

    private MaatApiCreateAssessment buildCreate(MeansAssessmentRequestDTO requestDTO) {
        return new MaatApiCreateAssessment()
                .withUsn(requestDTO.getUsn())
                .withRtCode(ofNullable(requestDTO.getReviewType())
                        .map(ReviewType::getCode).orElse(null))
                .withNworCode(requestDTO.getNewWorkReason().getCode())
                .withUserCreated(requestDTO.getUserSession().getUserName())
                .withIncomeUpliftRemoveDate(ofNullable(requestDTO.getIncomeEvidenceSummary())
                        .map(ApiIncomeEvidenceSummary::getUpliftRemovedDate).orElse(null))
                .withIncomeUpliftApplyDate(ofNullable(requestDTO.getIncomeEvidenceSummary())
                        .map(ApiIncomeEvidenceSummary::getUpliftAppliedDate).orElse(null));
    }

    private MaatApiUpdateAssessment buildUpdate(MeansAssessmentDTO assessment) {
        MeansAssessmentRequestDTO meansAssessment = assessment.getMeansAssessment();

        MaatApiUpdateAssessment updateAssessment = new MaatApiUpdateAssessment();

        if (AssessmentType.FULL.equals(meansAssessment.getAssessmentType())) {
            updateAssessment
                    .withFullAscrId(assessment.getAssessmentCriteria().getId())
                    .withFassFullStatus(assessment.getCurrentStatus().getStatus())
                    .withFullAssessmentDate(meansAssessment.getFullAssessmentDate())
                    .withFullResult(ofNullable(assessment.getFullAssessmentResult())
                            .map(FullAssessmentResult::getResult).orElse(null))
                    .withFullResultReason(ofNullable(assessment.getFullAssessmentResult())
                            .map(FullAssessmentResult::getReason).orElse(null))
                    .withFullAssessmentNotes(meansAssessment.getFullAssessmentNotes())
                    .withFullAdjustedLivingAllowance(assessment.getAdjustedLivingAllowance())
                    .withFullTotalAnnualDisposableIncome(assessment.getTotalAnnualDisposableIncome())
                    .withFullOtherHousingNote(meansAssessment.getOtherHousingNote())
                    .withFullTotalAggregatedExpenses(assessment.getTotalAggregatedExpense());
        }
        return updateAssessment
                .withUserModified(meansAssessment.getUserSession().getUserName())
                .withFinancialAssessmentId(meansAssessment.getFinancialAssessmentId());
    }
}
