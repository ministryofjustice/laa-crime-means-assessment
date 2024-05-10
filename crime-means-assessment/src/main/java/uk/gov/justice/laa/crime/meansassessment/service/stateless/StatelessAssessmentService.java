package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.common.model.meansassessment.stateless.Assessment;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.enums.meansassessment.AgeRange;
import uk.gov.justice.laa.crime.enums.meansassessment.StatelessRequestType;
import uk.gov.justice.laa.crime.meansassessment.Income;
import uk.gov.justice.laa.crime.meansassessment.Outgoing;
import uk.gov.justice.laa.crime.meansassessment.StatelessFullResult;
import uk.gov.justice.laa.crime.meansassessment.StatelessInitialResult;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaService;
import uk.gov.justice.laa.crime.meansassessment.service.BaseMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.FullAssessmentAvailabilityService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessDataAdapter.mapChildGroupings;

@Service
public class StatelessAssessmentService extends BaseMeansAssessmentService {

    private final MeansAssessmentServiceFactory meansAssessmentServiceFactory;
    private final FullAssessmentAvailabilityService fullAssessmentAvailabilityService;

    public StatelessAssessmentService(AssessmentCriteriaService assessmentCriteriaService,
                                      MeansAssessmentServiceFactory meansAssessmentServiceFactory,
                                      FullAssessmentAvailabilityService fullAssessmentAvailabilityService) {

        super(assessmentCriteriaService);
        this.meansAssessmentServiceFactory = meansAssessmentServiceFactory;
        this.fullAssessmentAvailabilityService = fullAssessmentAvailabilityService;
    }

    public StatelessResult execute(Assessment assessment,
                                   Map<AgeRange, Integer> childGroupings,
                                   List<Income> income,
                                   List<Outgoing> outgoings) {
        final var assessmentCriteriaEntity = assessmentCriteriaService.getAssessmentCriteria(
                assessment.getAssessmentDate(), assessment.getHasPartner(), false
        );
        var initialAnswer = initialResult(
                childGroupings,
                assessmentCriteriaEntity,
                assessment.getCaseType(),
                assessment.getMagistrateCourtOutcome(),
                income,
                null);

        if (assessment.getAssessmentType() == StatelessRequestType.INITIAL) {
            return new StatelessResult(null, initialAnswer);
        }
        if (initialAnswer.isFullAssessmentPossible()) {
            final var statelessFullResult =
                    fullResult(childGroupings, assessmentCriteriaEntity, assessment.getEligibilityCheckRequired(), assessment.getCaseType(), outgoings,
                            initialAnswer.getTotalAggregatedIncome());
            return new StatelessResult(statelessFullResult, initialAnswer);
        } else {
            return new StatelessResult(null, initialAnswer);
        }
    }

    public StatelessFullResult fullResult(@NotNull Map<AgeRange, Integer> childGroupings,
                                          AssessmentCriteriaEntity criteriaEntry,
                                          boolean eligibilityCheckRequired,
                                          CaseType caseType,
                                          List<Outgoing> outgoings,
                                          BigDecimal totalIncome) {
        final var children = mapChildGroupings(childGroupings, criteriaEntry.getAssessmentCriteriaChildWeightings());

        // assessmentStatus has to be set 'COMPLETE' otherwise the return value is null
        final MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO
                .builder()
                .assessmentStatus(CurrentStatus.COMPLETE)
                .childWeightings(children)
                .initTotalAggregatedIncome(totalIncome)
                .eligibilityCheckRequired(eligibilityCheckRequired)
                .build();

        final var totalOutgoings = calcOutgoingTotals(criteriaEntry, caseType, outgoings);
        final var fullAssessmentService = meansAssessmentServiceFactory.getService(AssessmentType.FULL);
        final var fullAssessmentResult = fullAssessmentService.execute(totalOutgoings, requestDTO, criteriaEntry);

        return StatelessFullResult.builder()
                .result(fullAssessmentResult.getFullAssessmentResult())
                .disposableIncome(fullAssessmentResult.getTotalAnnualDisposableIncome())
                .adjustedLivingAllowance(fullAssessmentResult.getAdjustedLivingAllowance())
                .totalAnnualAggregatedExpenditure(fullAssessmentResult.getTotalAggregatedExpense())
                .eligibilityThreshold(criteriaEntry.getEligibilityThreshold()).build();
    }

    public StatelessInitialResult initialResult(@NotNull Map<AgeRange, Integer> childGroupings,
                                                AssessmentCriteriaEntity criteriaEntry,
                                                CaseType caseType,
                                                MagCourtOutcome magCourtOutcome,
                                                List<Income> incomes,
                                                NewWorkReason newWorkReason) {

        final var children = mapChildGroupings(childGroupings, criteriaEntry.getAssessmentCriteriaChildWeightings());

        final var totalIncome = calcIncomeTotals(criteriaEntry, caseType, incomes);
        final var initMeansAssessmentService = meansAssessmentServiceFactory.getService(AssessmentType.INIT);

        // assessmentStatus has to be set 'COMPLETE' otherwise the return value is null
        final MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO
                .builder()
                .childWeightings(children)
                .assessmentStatus(CurrentStatus.COMPLETE)
                .build();
        final var result = initMeansAssessmentService.execute(totalIncome, requestDTO, criteriaEntry);

        final var fullAssessmentPossible = fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                caseType,
                magCourtOutcome,
                newWorkReason,
                result.getInitAssessmentResult()
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
