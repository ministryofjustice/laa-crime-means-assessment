package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.Assessment;
import uk.gov.justice.laa.crime.meansassessment.service.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static uk.gov.justice.laa.crime.meansassessment.service.stateless.DataAdapter.convertChildGroupings;

@RequiredArgsConstructor
@Service
public class StatelessAssessmentService extends BaseMeansAssessmentService {
    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MeansAssessmentServiceFactory meansAssessmentServiceFactory;
    private final FullAssessmentAvailabilityService fullAssessmentAvailabilityService;

    public StatelessResult execute(Assessment assessment, Map<AgeRange, Integer> childGroupings, List<Income> income, List<Outgoing> outgoings) {
        if (assessment.getAssessmentType() == StatelessRequestType.INITIAL) {
            return initialOnly(assessment.getAssessmentDate(), assessment.getHasPartner(),
                    assessment.getCaseType(), assessment.getMagistrateCourtOutcome(),
                    childGroupings, income);
        } else {
            return initialAndFull(
                    assessment.getAssessmentDate(), assessment.getHasPartner(), assessment.getEligibilityCheckRequired(),
                    assessment.getCaseType(), assessment.getMagistrateCourtOutcome(), childGroupings,
                    income, outgoings);
        }
    }

    private StatelessResult initialAndFull(@NotNull LocalDateTime date,
                                          boolean hasPartner,
                                          boolean isEligibilityCheckRequired,
                                          CaseType caseType,
                                          MagCourtOutcome magCourtOutcome,
                                          @NotNull Map<AgeRange, Integer> childGroupings,
                                          @NotNull List<Income> incomes,
                                          @NotNull List<Outgoing> outgoings) {
        final var criteriaEntry = assessmentCriteriaService.getAssessmentCriteria(date, hasPartner, false);

        final var totalIncome = incomeTotals(assessmentCriteriaService, criteriaEntry, caseType, incomes);

        var initialAnswer = initialResult(date, hasPartner, caseType, magCourtOutcome, totalIncome, childGroupings);

        if (initialAnswer.isFullAssessmentPossible()) {
            final var children = convertChildGroupings(childGroupings, criteriaEntry.getAssessmentCriteriaChildWeightings());

            // assessmentStatus has to be set 'COMPLETE' otherwise the return value is null
            final MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO
                    .builder()
                    .assessmentStatus(CurrentStatus.COMPLETE)
                    .childWeightings(children)
                    .initTotalAggregatedIncome(totalIncome)
                    .eligibilityCheckRequired(isEligibilityCheckRequired)
                    .build();
            final var totalOutgoings = outgoingTotals(assessmentCriteriaService, criteriaEntry, caseType, outgoings);
            final var service = meansAssessmentServiceFactory.getService(AssessmentType.FULL);
            final var result = service.execute(totalOutgoings, requestDTO, criteriaEntry);

            return new StatelessResult(
            new StatelessFullResult(result.getFullAssessmentResult(), result.getTotalAnnualDisposableIncome(),
                    result.getAdjustedIncomeValue(), result.getTotalAggregatedIncome(), result.getAdjustedLivingAllowance(),
                    result.getTotalAggregatedExpense(), criteriaEntry.getEligibilityThreshold()),
                    initialAnswer);
        }
        else {
            return new StatelessResult(
                    null, initialAnswer);
        }
    }

    private StatelessResult initialOnly(@NotNull LocalDateTime date,
                                       boolean hasPartner,
                                       CaseType caseType,
                                       MagCourtOutcome magCourtOutcome,
                                       @NotNull Map<AgeRange, Integer> childGroupings,
                                       @NotNull List<Income> incomes
    ) {
        final var criteriaEntry = assessmentCriteriaService.getAssessmentCriteria(date, hasPartner, false);
        final var totalIncome = incomeTotals(assessmentCriteriaService, criteriaEntry, caseType, incomes);

        var initialAssessment = initialResult(date, hasPartner, caseType, magCourtOutcome, totalIncome, childGroupings);
        return new StatelessResult(null, initialAssessment);
    }

    private StatelessInitialResult initialResult(@NotNull LocalDateTime date,
                              boolean hasPartner,
                              CaseType caseType,
                              MagCourtOutcome magCourtOutcome,
                              @NotNull BigDecimal totalIncome,
                              @NotNull Map<AgeRange, Integer> childGroupings) {
        final var criteriaEntry = assessmentCriteriaService.getAssessmentCriteria(date, hasPartner, false);

        final var children = convertChildGroupings(childGroupings, criteriaEntry.getAssessmentCriteriaChildWeightings());

        // assessmentStatus has to be set 'COMPLETE' otherwise the return value is null
        final MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO
                .builder()
                .childWeightings(children)
                .assessmentStatus(CurrentStatus.COMPLETE)
                .build();

        final var service = meansAssessmentServiceFactory.getService(AssessmentType.INIT);
        final var result = service.execute(totalIncome, requestDTO, criteriaEntry);
        final var fullAssessmentPossible = fullAssessmentAvailabilityService
                .isFullAssessmentAvailable(caseType, magCourtOutcome, null, result.getInitAssessmentResult());

        return new StatelessInitialResult(
                result.getInitAssessmentResult(),
                criteriaEntry.getInitialLowerThreshold(),
                criteriaEntry.getInitialUpperThreshold(),
                fullAssessmentPossible
        );
    }

    public static BigDecimal incomeTotals(AssessmentCriteriaService assessmentCriteriaService,
                                          AssessmentCriteriaEntity assessmentCriteria,
                                          CaseType caseType,
                                          @NotNull List<Income> incomes) {
        var sectionSummaries = DataAdapter.incomeSectionSummaries(assessmentCriteria, incomes);
        return totalFromSummaries(assessmentCriteriaService, assessmentCriteria, caseType, sectionSummaries);
    }

    public static BigDecimal outgoingTotals(AssessmentCriteriaService assessmentCriteriaService,
                                            AssessmentCriteriaEntity assessmentCriteria,
                                            CaseType caseType,
                                            @NotNull List<Outgoing> outgoings) {
        var sectionSummaries = DataAdapter.outgoingSectionSummaries(assessmentCriteria, outgoings);
        return totalFromSummaries(assessmentCriteriaService, assessmentCriteria, caseType, sectionSummaries);
    }

    private static BigDecimal totalFromSummaries(AssessmentCriteriaService assessmentCriteriaService,
                                                 AssessmentCriteriaEntity assessmentCriteria,
                                                 CaseType caseType,
                                                 List<ApiAssessmentSectionSummary> sectionSummaries) {
        var requestDTO = MeansAssessmentRequestDTO
                .builder()
                .sectionSummaries(sectionSummaries)
                .caseType(caseType)
                .build();
        return calculateSummariesTotal(assessmentCriteriaService, requestDTO, assessmentCriteria);
    }
}
