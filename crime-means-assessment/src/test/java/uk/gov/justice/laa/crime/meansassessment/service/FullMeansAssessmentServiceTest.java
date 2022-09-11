package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FullMeansAssessmentServiceTest {

    private final BigDecimal EXPECTED_ADJUSTED_LIVING_ALLOWANCE = BigDecimal.valueOf(6000)
            .setScale(2, RoundingMode.HALF_UP);

    private final AssessmentCriteriaEntity assessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();

    private final MeansAssessmentRequestDTO meansAssessment =
            TestModelDataBuilder.getMeansAssessmentRequestDTO(true);

    @InjectMocks
    private FullMeansAssessmentService fullMeansAssessmentService;

    @Mock
    private CrownCourtEligibilityService crownCourtEligibilityService;

    @Mock
    private AssessmentCriteriaChildWeightingService childWeightingService;

    @Before
    public void setUp() {
        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(TestModelDataBuilder.TEST_TOTAL_CHILD_WEIGHTING);

        meansAssessment.setInitTotalAggregatedIncome(TestModelDataBuilder.TEST_AGGREGATED_INCOME);
    }

    @Test
    public void givenCompletedAssessment_whenDoFullAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {
        setupEligibilityCheckStubbing(false);
        MeansAssessmentDTO result =
                fullMeansAssessmentService.execute(TestModelDataBuilder.TEST_AGGREGATED_EXPENDITURE, meansAssessment, assessmentCriteria);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getFullAssessmentResult()).isEqualTo(FullAssessmentResult.PASS);
            assertThat(result.getTotalAggregatedExpense()).isEqualByComparingTo(TestModelDataBuilder.TEST_AGGREGATED_EXPENDITURE);
            assertThat(result.getTotalAnnualDisposableIncome()).isEqualByComparingTo(TestModelDataBuilder.TEST_DISPOSABLE_INCOME);
            assertThat(result.getAdjustedLivingAllowance()).isEqualByComparingTo(TestModelDataBuilder.TEST_ADJUSTED_LIVING_ALLOWANCE);
        });
    }

    @Test
    public void givenIncompleteAssessment_whenDoFullAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {

        meansAssessment.setAssessmentStatus(CurrentStatus.IN_PROGRESS);
        MeansAssessmentDTO result =
                fullMeansAssessmentService.execute(TestModelDataBuilder.TEST_AGGREGATED_EXPENDITURE, meansAssessment, assessmentCriteria);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getFullAssessmentResult()).isNull();
            assertThat(result.getTotalAggregatedExpense()).isEqualByComparingTo(TestModelDataBuilder.TEST_AGGREGATED_EXPENDITURE);
            assertThat(result.getTotalAnnualDisposableIncome()).isEqualByComparingTo(TestModelDataBuilder.TEST_DISPOSABLE_INCOME);
            assertThat(result.getAdjustedLivingAllowance()).isEqualByComparingTo(TestModelDataBuilder.TEST_ADJUSTED_LIVING_ALLOWANCE);
        });
    }

    @Test
    public void givenCorrectParameters_whenGetDisposableIncomeIsInvoked_thenCalculationIsCorrect() {
        meansAssessment.setInitTotalAggregatedIncome(TestModelDataBuilder.TEST_AGGREGATED_INCOME);
        BigDecimal result = fullMeansAssessmentService.getDisposableIncome(
                meansAssessment, TestModelDataBuilder.TEST_AGGREGATED_EXPENDITURE, TestModelDataBuilder.TEST_ADJUSTED_LIVING_ALLOWANCE
        );
        assertThat(result).isEqualByComparingTo(TestModelDataBuilder.TEST_DISPOSABLE_INCOME);
    }

    @Test
    public void givenCorrectParameters_whenGetAdjustedLivingAllowanceIsInvoked_thenCalculationIsCorrect() {
        when(childWeightingService.getTotalChildWeighting(anyList(), any(AssessmentCriteriaEntity.class))).thenReturn(
                TestModelDataBuilder.TEST_TOTAL_CHILD_WEIGHTING
        );
        BigDecimal result = fullMeansAssessmentService.getAdjustedLivingAllowance(meansAssessment, assessmentCriteria);
        assertThat(result).isEqualByComparingTo(EXPECTED_ADJUSTED_LIVING_ALLOWANCE);
    }

    private void setupEligibilityCheckStubbing(boolean returnValue) {
        when(crownCourtEligibilityService.isEligibilityCheckRequired(
                any(MeansAssessmentRequestDTO.class)
        )).thenReturn(returnValue);
    }

    @Test
    public void givenDisposableIncomeAboveThreshold_whenGetResultIsInvoked_thenResultIsFail() {
        setupEligibilityCheckStubbing(false);
        BigDecimal disposableIncome =
                assessmentCriteria.getFullThreshold().add(BigDecimal.valueOf(0.01));
        FullAssessmentResult result = fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria);
        assertThat(result).isEqualTo(FullAssessmentResult.FAIL);
    }

    @Test
    public void givenDisposableIncomeBelowThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        setupEligibilityCheckStubbing(false);
        BigDecimal disposableIncome =
                assessmentCriteria.getFullThreshold().subtract(BigDecimal.valueOf(0.01));
        FullAssessmentResult result = fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria);
        assertThat(result).isEqualTo(FullAssessmentResult.PASS);
    }

    @Test
    public void givenEligibilityCheckRequiredAndIncomeBelowThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        setupEligibilityCheckStubbing(true);
        BigDecimal disposableIncome =
                assessmentCriteria.getEligibilityThreshold().subtract(BigDecimal.valueOf(0.01));
        assertThat(fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria))
                .isEqualTo(FullAssessmentResult.FAIL);
    }

    @Test
    public void givenEligibilityCheckRequiredAndEqualsThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        setupEligibilityCheckStubbing(true);
        BigDecimal disposableIncome =
                assessmentCriteria.getEligibilityThreshold();
        assertThat(fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria))
                .isEqualTo(FullAssessmentResult.INEL);
    }

    @Test
    public void givenEligibilityCheckRequiredAndIncomeAboveThreshold_whenGetResultIsInvoked_thenResultIsPass() {
        setupEligibilityCheckStubbing(true);
        BigDecimal disposableIncome =
                assessmentCriteria.getEligibilityThreshold().add(BigDecimal.valueOf(0.01));
        assertThat(fullMeansAssessmentService.getResult(disposableIncome, meansAssessment, assessmentCriteria))
                .isEqualTo(FullAssessmentResult.INEL);
    }

}
