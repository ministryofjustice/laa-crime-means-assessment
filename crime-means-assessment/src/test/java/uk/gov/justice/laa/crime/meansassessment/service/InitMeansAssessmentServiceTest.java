package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InitMeansAssessmentServiceTest {

    private final AssessmentCriteriaEntity assessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();

    private final BigDecimal upperThreshold = TestModelDataBuilder.TEST_INITIAL_UPPER_THRESHOLD;
    private final BigDecimal lowerThreshold = TestModelDataBuilder.TEST_INITIAL_LOWER_THRESHOLD;

    private final MeansAssessmentRequestDTO meansAssessment =
            TestModelDataBuilder.getMeansAssessmentRequestDTO(true);

    @Spy
    @InjectMocks
    private InitMeansAssessmentService initMeansAssessmentService;

    @Mock
    private AssessmentCriteriaChildWeightingService childWeightingService;

    @Before
    public void setUp() {
        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(TestModelDataBuilder.TEST_TOTAL_CHILD_WEIGHTING);
    }

    @Test
    public void givenCompletedAssessment_whenDoInitAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {
        MeansAssessmentDTO result =
                initMeansAssessmentService.execute(TestModelDataBuilder.TEST_AGGREGATED_INCOME, meansAssessment, assessmentCriteria);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getInitAssessmentResult()).isEqualTo(InitAssessmentResult.FULL);
            assertThat(result.getAdjustedIncomeValue()).isEqualTo(TestModelDataBuilder.TEST_ADJUSTED_INCOME);
            assertThat(result.getTotalAggregatedIncome()).isEqualTo(TestModelDataBuilder.TEST_AGGREGATED_INCOME);
        });
    }

    @Test
    public void givenIncompleteAssessment_whenDoInitAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {
        meansAssessment.setAssessmentStatus(CurrentStatus.IN_PROGRESS);
        MeansAssessmentDTO result =
                initMeansAssessmentService.execute(TestModelDataBuilder.TEST_AGGREGATED_INCOME, meansAssessment, assessmentCriteria);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getInitAssessmentResult()).isNull();
            assertThat(result.getAdjustedIncomeValue()).isEqualTo(TestModelDataBuilder.TEST_ADJUSTED_INCOME);
            assertThat(result.getTotalAggregatedIncome()).isEqualTo(TestModelDataBuilder.TEST_AGGREGATED_INCOME);
        });
    }

    @Test
    public void givenIncomeBelowLowerThreshold_whenGetAssessmentResultIsInvoked_thenResultIsPass() {
        BigDecimal adjustedIncome = lowerThreshold.subtract(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, "FMA");
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.PASS));
    }

    @Test
    public void givenIncomeBetweenThresholds_whenGetAssessmentResultIsInvoked_thenResultIsFull() {
        BigDecimal adjustedIncome = lowerThreshold.add(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, "FMA");
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.FULL));
    }

    @Test
    public void givenIncomeAboveUpperThreshold_whenGetAssessmentResultIsInvoked_thenResultIsFail() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, "FMA");
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.FAIL));
    }

    @Test
    public void givenIncomeAboveUpperThresholdAndHardshipApplication_whenGetAssessmentResultIsInvoked_thenResultIsHardship() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, "HR");
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.HARDSHIP));
    }

    @Test
    public void givenPositiveAnnualTotal_whenGetAdjustedIncomeIsInvoked_thenAdjustedIncomeIsCalculated() {
        BigDecimal annualTotal = BigDecimal.valueOf(11000);
        BigDecimal totalChildWeighting = BigDecimal.valueOf(0.5);
        BigDecimal combinedWeightingFactor =
                TestModelDataBuilder.TEST_APPLICANT_WEIGHTING_FACTOR.add(
                        TestModelDataBuilder.TEST_PARTNER_WEIGHTING_FACTOR
                ).add(totalChildWeighting);

        BigDecimal expected = annualTotal.divide(combinedWeightingFactor, RoundingMode.UP);

        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(totalChildWeighting);

        assertThat(initMeansAssessmentService.getAdjustedIncome(
                TestModelDataBuilder.getMeansAssessmentRequestDTO(true), assessmentCriteria, annualTotal)
        ).isEqualTo(expected);
    }

    @Test
    public void givenZeroAnnualTotal_whenGetAdjustedIncomeIsInvoked_thenReturnsZero() {
        BigDecimal annualTotal = BigDecimal.ZERO;
        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(BigDecimal.ZERO);
        assertThat(initMeansAssessmentService.getAdjustedIncome(
                TestModelDataBuilder.getMeansAssessmentRequestDTO(true), assessmentCriteria, annualTotal)
        ).isEqualTo(BigDecimal.ZERO);
    }
}
