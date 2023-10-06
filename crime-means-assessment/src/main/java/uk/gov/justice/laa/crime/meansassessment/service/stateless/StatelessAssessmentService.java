package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.stateless.Assessment;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.service.BaseMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.FullAssessmentAvailabilityService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.StatelessRequestType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessDataAdapter.mapChildGroupings;

@Service
public class StatelessAssessmentService extends BaseMeansAssessmentService {

    private final AssessmentCriteriaService assessmentCriteriaService;
    private final MeansAssessmentServiceFactory meansAssessmentServiceFactory;
    private final FullAssessmentAvailabilityService fullAssessmentAvailabilityService;

    public StatelessAssessmentService(AssessmentCriteriaService assessmentCriteriaService,
                                      MeansAssessmentServiceFactory meansAssessmentServiceFactory,
                                      FullAssessmentAvailabilityService fullAssessmentAvailabilityService) {

        super(assessmentCriteriaService);
        this.assessmentCriteriaService = assessmentCriteriaService;
        this.meansAssessmentServiceFactory = meansAssessmentServiceFactory;
        this.fullAssessmentAvailabilityService = fullAssessmentAvailabilityService;
    }

    public StatelessResult execute(Assessment assessment,
                                   Map<AgeRange, Integer> childGroupings,
                                   List<Income> income,
                                   List<Outgoing> outgoings) {

        if (assessment.getAssessmentType() == StatelessRequestType.INITIAL) {
            return initialOnly(assessment, childGroupings, income);
        } else {
            return initialAndFull(assessment, childGroupings, income, outgoings);
        }
    }

    private StatelessResult initialAndFull(@NotNull Assessment assessment,
                                           @NotNull Map<AgeRange, Integer> childGroupings,
                                           @NotNull List<Income> incomes,
                                           @NotNull List<Outgoing> outgoings) {

        final var criteriaEntry = assessmentCriteriaService.getAssessmentCriteria(
                assessment.getAssessmentDate(), assessment.getHasPartner(), false
        );
        final var totalIncome = calcIncomeTotals(criteriaEntry, assessment.getCaseType(), incomes);
        var initialAnswer = initialResult(assessment, totalIncome, childGroupings);

        if (initialAnswer.isFullAssessmentPossible()) {
            final var children = mapChildGroupings(childGroupings, criteriaEntry.getAssessmentCriteriaChildWeightings());

            // assessmentStatus has to be set 'COMPLETE' otherwise the return value is null
            final MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO
                    .builder()
                    .assessmentStatus(CurrentStatus.COMPLETE)
                    .childWeightings(children)
                    .initTotalAggregatedIncome(totalIncome)
                    .eligibilityCheckRequired(assessment.getEligibilityCheckRequired())
                    .build();

            final var totalOutgoings = calcOutgoingTotals(criteriaEntry, assessment.getCaseType(), outgoings);
            final var fullAssessmentService = meansAssessmentServiceFactory.getService(AssessmentType.FULL);
            final var fullAssessmentResult = fullAssessmentService.execute(totalOutgoings, requestDTO, criteriaEntry);

            final var statelessFullResult = StatelessFullResult.builder()
                    .result(fullAssessmentResult.getFullAssessmentResult())
                    .disposableIncome(fullAssessmentResult.getTotalAnnualDisposableIncome())
                    .adjustedLivingAllowance(fullAssessmentResult.getAdjustedLivingAllowance())
                    .totalAnnualAggregatedExpenditure(fullAssessmentResult.getTotalAggregatedExpense())
                    .eligibilityThreshold(criteriaEntry.getEligibilityThreshold()).build();
            return new StatelessResult(statelessFullResult, initialAnswer);
        } else {
            return new StatelessResult(null, initialAnswer);
        }
    }

    private StatelessResult initialOnly(@NotNull Assessment assessment,
                                        @NotNull Map<AgeRange, Integer> childGroupings,
                                        @NotNull List<Income> incomes) {

        final var criteriaEntry = assessmentCriteriaService.getAssessmentCriteria(
                assessment.getAssessmentDate(), assessment.getHasPartner(), false
        );
        final var totalIncome = calcIncomeTotals(criteriaEntry, assessment.getCaseType(), incomes);
        var initialAssessment = initialResult(assessment, totalIncome, childGroupings);
        return new StatelessResult(null, initialAssessment);
    }

    private StatelessInitialResult initialResult(@NotNull Assessment assessment,
                                                 @NotNull BigDecimal totalIncome,
                                                 @NotNull Map<AgeRange, Integer> childGroupings) {

        final var criteriaEntry = assessmentCriteriaService.getAssessmentCriteria(
                assessment.getAssessmentDate(), assessment.getHasPartner(), false
        );
        final var children = mapChildGroupings(childGroupings, criteriaEntry.getAssessmentCriteriaChildWeightings());

        // assessmentStatus has to be set 'COMPLETE' otherwise the return value is null
        final MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO
                .builder()
                .childWeightings(children)
                .assessmentStatus(CurrentStatus.COMPLETE)
                .build();

        final var service = meansAssessmentServiceFactory.getService(AssessmentType.INIT);
        final var result = service.execute(totalIncome, requestDTO, criteriaEntry);
        final var fullAssessmentPossible = fullAssessmentAvailabilityService
                .isFullAssessmentAvailable(assessment.getCaseType(),
                        assessment.getMagistrateCourtOutcome(),
                        null, result.getInitAssessmentResult()
                );

        return StatelessInitialResult.builder()
                .result(result.getInitAssessmentResult())
                .lowerThreshold(criteriaEntry.getInitialLowerThreshold())
                .upperThreshold(criteriaEntry.getInitialUpperThreshold())
                .fullAssessmentPossible(fullAssessmentPossible)
                .adjustedIncomeValue(result.getAdjustedIncomeValue())
                .totalAggregatedIncome(result.getTotalAggregatedIncome())
                .build();
    }

    private BigDecimal calcIncomeTotals(AssessmentCriteriaEntity assessmentCriteria,
                                        CaseType caseType,
                                        @NotNull List<Income> incomes) {
        return calcTotalFromSummaries(
                assessmentCriteria,
                caseType,
                StatelessDataAdapter.mapIncomesToSectionSummaries(assessmentCriteria, incomes)
        );
    }

    private BigDecimal calcOutgoingTotals(AssessmentCriteriaEntity assessmentCriteria,
                                          CaseType caseType,
                                          @NotNull List<Outgoing> outgoings) {
        return calcTotalFromSummaries(
                assessmentCriteria,
                caseType,
                StatelessDataAdapter.mapOutgoingsToSectionSummaries(assessmentCriteria, outgoings)
        );
    }

    private BigDecimal calcTotalFromSummaries(AssessmentCriteriaEntity assessmentCriteria,
                                              CaseType caseType,
                                              List<ApiAssessmentSectionSummary> sectionSummaries) {
        var requestDTO = MeansAssessmentRequestDTO
                .builder()
                .sectionSummaries(sectionSummaries)
                .caseType(caseType)
                .build();

        return calculateSummariesTotal(requestDTO, assessmentCriteria);
    }
}
