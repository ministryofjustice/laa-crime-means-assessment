package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitMeansAssessmentServiceTest {

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

    private void setupChildWeightingMock() {
        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(TestModelDataBuilder.TEST_TOTAL_CHILD_WEIGHTING);
    }

    @Test
    void givenCompletedAssessment_whenDoInitAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {
        setupChildWeightingMock();
        MeansAssessmentDTO result =
                initMeansAssessmentService.execute(TestModelDataBuilder.TEST_AGGREGATED_INCOME, meansAssessment, assessmentCriteria);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getInitAssessmentResult()).isEqualTo(InitAssessmentResult.FULL);
            assertThat(result.getAdjustedIncomeValue())
                    .isEqualTo(TestModelDataBuilder.TEST_ADJUSTED_INCOME.setScale(2, RoundingMode.HALF_UP));
            assertThat(result.getTotalAggregatedIncome()).isEqualTo(TestModelDataBuilder.TEST_AGGREGATED_INCOME);
        });
    }

    @Test
    void givenIncompleteAssessment_whenDoInitAssessmentIsInvoked_thenMeansAssessmentDTOIsReturned() {
        setupChildWeightingMock();
        meansAssessment.setAssessmentStatus(CurrentStatus.IN_PROGRESS);
        MeansAssessmentDTO result =
                initMeansAssessmentService.execute(TestModelDataBuilder.TEST_AGGREGATED_INCOME, meansAssessment, assessmentCriteria);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getCurrentStatus()).isEqualTo(meansAssessment.getAssessmentStatus());
            assertThat(result.getInitAssessmentResult()).isNull();
            assertThat(result.getAdjustedIncomeValue())
                    .isEqualTo(TestModelDataBuilder.TEST_ADJUSTED_INCOME.setScale(2, RoundingMode.HALF_UP));
            assertThat(result.getTotalAggregatedIncome()).isEqualTo(TestModelDataBuilder.TEST_AGGREGATED_INCOME);
        });
    }

    @Test
    void givenIncomeBelowLowerThreshold_whenGetAssessmentResultIsInvoked_thenResultIsPass() {
        BigDecimal adjustedIncome = lowerThreshold.subtract(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, NewWorkReason.FMA);
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.PASS));
    }

    @Test
    void givenIncomeBetweenThresholds_whenGetAssessmentResultIsInvoked_thenResultIsFull() {
        BigDecimal adjustedIncome = lowerThreshold.add(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, NewWorkReason.FMA);
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.FULL));
    }

    @Test
    void givenIncomeAboveUpperThreshold_whenGetAssessmentResultIsInvoked_thenResultIsFail() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, NewWorkReason.FMA);
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.FAIL));
    }

    @Test
    void givenIncomeAboveUpperThresholdAndHardshipApplication_whenGetAssessmentResultIsInvoked_thenResultIsHardship() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        InitAssessmentResult result =
                initMeansAssessmentService.getResult(adjustedIncome, assessmentCriteria, NewWorkReason.HR);
        SoftAssertions.assertSoftly(softly -> assertThat(result).isEqualTo(InitAssessmentResult.HARDSHIP));
    }

    @Test
    void givenPositiveAnnualTotal_whenGetAdjustedIncomeIsInvoked_thenAdjustedIncomeIsCalculated() {
        BigDecimal annualTotal = BigDecimal.valueOf(11000.00);
        BigDecimal totalChildWeighting = BigDecimal.valueOf(0.50);
        BigDecimal applicantWeightingFactor = TestModelDataBuilder.TEST_APPLICANT_WEIGHTING_FACTOR;
        BigDecimal partnerWeightingFactor = TestModelDataBuilder.TEST_PARTNER_WEIGHTING_FACTOR;
        BigDecimal expected = BigDecimal.valueOf(12222.22);

        assertAdjustedIncomeIsCorrect(annualTotal, totalChildWeighting, applicantWeightingFactor, partnerWeightingFactor, expected);
    }

    @Test
    void givenPositiveAnnualTotalRequiringRounding_whenGetAdjustedIncomeIsInvoked_thenAdjustedIncomeIsCalculated() {
        BigDecimal annualTotal = BigDecimal.valueOf(36613.02);
        BigDecimal totalChildWeighting = BigDecimal.valueOf(0);
        BigDecimal applicantWeightingFactor = BigDecimal.valueOf(1.0);
        BigDecimal partnerWeightingFactor = BigDecimal.valueOf(0.64);
        BigDecimal expected = BigDecimal.valueOf(22325.01);

        assertAdjustedIncomeIsCorrect(annualTotal, totalChildWeighting, applicantWeightingFactor, partnerWeightingFactor, expected);
    }

    @Test
    void givenPositiveAnnualTotalChildWeightingRoundingError_whenGetAdjustedIncomeIsInvoked_thenAdjustedIncomeIsCalculated() {
        BigDecimal annualTotal = BigDecimal.valueOf(37506.00);
        BigDecimal totalChildWeighting = BigDecimal.valueOf(0.68);
        BigDecimal applicantWeightingFactor = BigDecimal.valueOf(1.00);
        BigDecimal partnerWeightingFactor = BigDecimal.valueOf(0.64);
        BigDecimal expected = BigDecimal.valueOf(16166.38);

        assertAdjustedIncomeIsCorrect(annualTotal, totalChildWeighting, applicantWeightingFactor, partnerWeightingFactor, expected);
    }

    @Test
    void givenZeroAnnualTotal_whenGetAdjustedIncomeIsInvoked_thenReturnsZero() {
        BigDecimal annualTotal = BigDecimal.ZERO;
        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(BigDecimal.ZERO);
        assertThat(initMeansAssessmentService.getAdjustedIncome(
                TestModelDataBuilder.getMeansAssessmentRequestDTO(true), assessmentCriteria, annualTotal)
        ).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    private void assertAdjustedIncomeIsCorrect(
            BigDecimal annualTotal,
            BigDecimal totalChildWeighting,
            BigDecimal applicantWeighting,
            BigDecimal partnerWeighting,
            BigDecimal expectedValue) {


        AssessmentCriteriaEntity testAssessmentCriteria =
                AssessmentCriteriaEntity.builder()
                        .applicantWeightingFactor(applicantWeighting)
                        .partnerWeightingFactor(partnerWeighting)
                        .build();

        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(totalChildWeighting);

        assertThat(initMeansAssessmentService.getAdjustedIncome(
                TestModelDataBuilder.getMeansAssessmentRequestDTO(true), testAssessmentCriteria, annualTotal)
        ).isEqualTo(expectedValue);
    }
}
