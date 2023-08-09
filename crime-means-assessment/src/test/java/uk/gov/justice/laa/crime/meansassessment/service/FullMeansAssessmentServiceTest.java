package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.FullAssessmentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FullMeansAssessmentServiceTest {

    private final BigDecimal EXPECTED_ADJUSTED_LIVING_ALLOWANCE = BigDecimal.valueOf(6000)
            .setScale(2, RoundingMode.HALF_UP);

    private final AssessmentCriteriaEntity assessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();

    private MeansAssessmentRequestDTO meansAssessment;

    @InjectMocks
    private FullMeansAssessmentService fullMeansAssessmentService;

    @Mock
    private AssessmentCriteriaChildWeightingService childWeightingService;

    @BeforeEach
    void setUp() {
        lenient().when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(TestModelDataBuilder.TEST_TOTAL_CHILD_WEIGHTING);

        meansAssessment = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        meansAssessment.setInitTotalAggregatedIncome(TestModelDataBuilder.TEST_AGGREGATED_INCOME);
    }

    @Test
    void givenCompletedAssessment_whenDoFullAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {
        MeansAssessmentDTO result =
                fullMeansAssessmentService.execute(TestModelDataBuilder.TEST_TOTAL_EXPENDITURE, meansAssessment, assessmentCriteria);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getFullAssessmentResult()).isEqualTo(FullAssessmentResult.PASS);
            assertThat(result.getTotalAggregatedExpense())
                    .isEqualByComparingTo(TestModelDataBuilder.TEST_TOTAL_AGGREGATED_EXPENDITURE);
            assertThat(result.getTotalAnnualDisposableIncome())
                    .isEqualByComparingTo(TestModelDataBuilder.TEST_DISPOSABLE_INCOME);
            assertThat(result.getAdjustedLivingAllowance())
                    .isEqualByComparingTo(TestModelDataBuilder.TEST_ADJUSTED_LIVING_ALLOWANCE);
        });
    }

    @Test
    void givenIncompleteAssessment_whenDoFullAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {

        meansAssessment.setAssessmentStatus(CurrentStatus.IN_PROGRESS);
        MeansAssessmentDTO result =
                fullMeansAssessmentService.execute(
                        TestModelDataBuilder.TEST_TOTAL_EXPENDITURE, meansAssessment, assessmentCriteria
                );

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getFullAssessmentResult()).isNull();
            assertThat(result.getTotalAggregatedExpense())
                    .isEqualByComparingTo(TestModelDataBuilder.TEST_TOTAL_AGGREGATED_EXPENDITURE);
            assertThat(result.getTotalAnnualDisposableIncome())
                    .isEqualByComparingTo(TestModelDataBuilder.TEST_DISPOSABLE_INCOME);
            assertThat(result.getAdjustedLivingAllowance())
                    .isEqualByComparingTo(TestModelDataBuilder.TEST_ADJUSTED_LIVING_ALLOWANCE);
        });
    }

    @Test
    void givenCorrectParameters_whenGetDisposableIncomeIsInvoked_thenCalculationIsCorrect() {
        BigDecimal result = fullMeansAssessmentService.getDisposableIncome(
                TestModelDataBuilder.TEST_AGGREGATED_INCOME,
                TestModelDataBuilder.TEST_TOTAL_EXPENDITURE,
                TestModelDataBuilder.TEST_ADJUSTED_LIVING_ALLOWANCE
        );
        assertThat(result).isEqualByComparingTo(TestModelDataBuilder.TEST_DISPOSABLE_INCOME);
    }

    @Test
    void givenAggregatedAndDisposableIncome_whenGetAnnualAggregatedExpenditureIsInvoked_thenResultIsCorrect() {
        BigDecimal aggregatedExpenditure = fullMeansAssessmentService.getAnnualAggregatedExpenditure(
                TestModelDataBuilder.TEST_AGGREGATED_INCOME, TestModelDataBuilder.TEST_DISPOSABLE_INCOME
        );
        assertThat(aggregatedExpenditure).isEqualByComparingTo(TestModelDataBuilder.TEST_TOTAL_AGGREGATED_EXPENDITURE);
    }

    @Test
    void givenCorrectParameters_whenGetAdjustedLivingAllowanceIsInvoked_thenCalculationIsCorrect() {
        when(childWeightingService.getTotalChildWeighting(anyList(), any(AssessmentCriteriaEntity.class)))
                .thenReturn(TestModelDataBuilder.TEST_TOTAL_CHILD_WEIGHTING);
        BigDecimal result = fullMeansAssessmentService.getAdjustedLivingAllowance(meansAssessment, assessmentCriteria);
        assertThat(result).isEqualByComparingTo(EXPECTED_ADJUSTED_LIVING_ALLOWANCE);
    }

    @Test
    void givenDisposableIncomeAboveThreshold_whenGetResultIsInvoked_thenResultIsFail() {
        BigDecimal disposableIncome =
                assessmentCriteria.getFullThreshold().add(BigDecimal.valueOf(0.01));
        FullAssessmentResult result =
                fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria);
        assertThat(result).isEqualTo(FullAssessmentResult.FAIL);
    }

    @Test
    void givenDisposableIncomeBelowThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        BigDecimal disposableIncome =
                assessmentCriteria.getFullThreshold().subtract(BigDecimal.valueOf(0.01));
        FullAssessmentResult result =
                fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria);
        assertThat(result).isEqualTo(FullAssessmentResult.PASS);
    }

    @Test
    void givenEligibilityCheckRequiredAndIncomeBelowThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        BigDecimal disposableIncome =
                assessmentCriteria.getEligibilityThreshold().subtract(BigDecimal.valueOf(0.01));
        assertThat(fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria))
                .isEqualTo(FullAssessmentResult.FAIL);
    }

    @Test
    void givenEligibilityCheckRequiredAndEqualsThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        BigDecimal disposableIncome =
                assessmentCriteria.getEligibilityThreshold();
        meansAssessment.setEligibilityCheckRequired(true);
        assertThat(fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria))
                .isEqualTo(FullAssessmentResult.INEL);
    }

    @Test
    void givenEligibilityCheckRequiredAndIncomeAboveThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        BigDecimal disposableIncome =
                assessmentCriteria.getEligibilityThreshold().add(BigDecimal.valueOf(0.01));
        meansAssessment.setEligibilityCheckRequired(true);
        assertThat(fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria))
                .isEqualTo(FullAssessmentResult.INEL);
    }

}
