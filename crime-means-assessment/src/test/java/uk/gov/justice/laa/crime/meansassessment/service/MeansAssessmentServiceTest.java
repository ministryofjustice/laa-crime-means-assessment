package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentServiceTest {

    private AssessmentCriteriaEntity assessmentCriteria;
    private BigDecimal upperThreshold = TestModelDataBuilder.TEST_INITIAL_UPPER_THRESHOLD;
    private BigDecimal lowerThreshold = TestModelDataBuilder.TEST_INITIAL_LOWER_THRESHOLD;

    @Spy
    @InjectMocks
    private MeansAssessmentService meansAssessmentService;

    @Mock
    private AssessmentCriteriaService assessmentCriteriaService;

    @Mock
    private AssessmentCriteriaChildWeightingService childWeightingService;

    @Before
    public void setUp() {
        assessmentCriteria = TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();
    }

    @Test
    public void givenIncompleteStatus_whenGetAssessmentResultIsInvoked_thenResultFieldsAreNull() {
        BigDecimal adjustedIncome = BigDecimal.valueOf(1000);

        MeansAssessmentResultDTO result =
                meansAssessmentService.getAssessmentResult(CurrentStatus.IN_PROGRESS, adjustedIncome, upperThreshold, lowerThreshold, "FMA");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getResult()).isNull();
            assertThat(result.getReason()).isNull();
        });
    }

    @Test
    public void givenCompleteStatusAndIncomeBelowLowerThreshold_whenGetAssessmentResultIsInvoked_thenResultIsPass() {
        BigDecimal adjustedIncome = lowerThreshold.subtract(BigDecimal.valueOf(0.01));
        MeansAssessmentResultDTO result =
                meansAssessmentService.getAssessmentResult(CurrentStatus.COMPLETE, adjustedIncome, upperThreshold, lowerThreshold, "FMA");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getResult()).isEqualTo("PASS");
            assertThat(result.getReason()).isEqualTo("Gross income below the lower threshold");
        });
    }

    @Test
    public void givenCompleteStatusAndIncomeBetweenThresholds_whenGetAssessmentResultIsInvoked_thenResultIsFull() {
        BigDecimal adjustedIncome = lowerThreshold.add(BigDecimal.valueOf(0.01));
        MeansAssessmentResultDTO result =
                meansAssessmentService.getAssessmentResult(CurrentStatus.COMPLETE, adjustedIncome, upperThreshold, lowerThreshold, "FMA");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getResult()).isEqualTo("FULL");
            assertThat(result.getReason()).isEqualTo("Gross income in between the upper and lower thresholds");
        });
    }

    @Test
    public void givenCompleteStatusAndIncomeAboveUpperThreshold_whenGetAssessmentResultIsInvoked_thenResultIsFail() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        MeansAssessmentResultDTO result =
                meansAssessmentService.getAssessmentResult(CurrentStatus.COMPLETE, adjustedIncome, upperThreshold, lowerThreshold, "FMA");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getResult()).isEqualTo("FAIL");
            assertThat(result.getReason()).isEqualTo("Gross income above the upper threshold");
        });
    }

    @Test
    public void givenCompleteStatusAndIncomeAboveUpperThresholdAndHardshipApplication_whenGetAssessmentResultIsInvoked_thenResultIsFail() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        MeansAssessmentResultDTO result =
                meansAssessmentService.getAssessmentResult(CurrentStatus.COMPLETE, adjustedIncome, upperThreshold, lowerThreshold, "HR");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result.getResult()).isEqualTo("HR");
            assertThat(result.getReason()).isNull();
        });
    }

    @Test
    public void givenNullApplicantAmount_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail().withApplicantAmount(BigDecimal.ZERO);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenNullPartnerAmountAndPartnerFlag_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail().withPartnerAmount(BigDecimal.ZERO);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail, true);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenValidApplicantAmount_whenGetDetailTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withApplicantAmount(BigDecimal.TEN)
                .withApplicantFrequency(Frequency.MONTHLY);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail);
        assertThat(total).isEqualTo(BigDecimal.valueOf(120));
    }

    @Test
    public void givenValidPartnerAmountAndPartnerFlag_whenGetDetailTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withPartnerAmount(BigDecimal.TEN)
                .withPartnerFrequency(Frequency.MONTHLY);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail);
        assertThat(total).isEqualTo(BigDecimal.valueOf(120));
    }

    @Test
    public void givenSingleSectionSingleDetailNoPartner_whenGetAnnualTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = TestModelDataBuilder.getApiAssessmentSectionSummary();
        BigDecimal annualTotal = meansAssessmentService.getAnnualTotal(CaseType.EITHER_WAY, assessmentCriteria, List.of(section));
        assertThat(annualTotal).isEqualTo(
                TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                )
        );
    }

    @Test
    public void givenSingleSectionTwoDetailNoPartner_whenGetAnnualTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = TestModelDataBuilder.getApiAssessmentSectionSummary();
        section.getAssessmentDetails().add(
                new ApiAssessmentDetail()
                        .withApplicantAmount(BigDecimal.TEN)
                        .withApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY)
        );

        BigDecimal annualTotal = meansAssessmentService.getAnnualTotal(CaseType.EITHER_WAY, assessmentCriteria, List.of(section));
        assertThat(annualTotal).isEqualTo(
                TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                ).add(BigDecimal.TEN.multiply(
                                BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                        )
                )
        );
    }

    @Test
    public void givenSingleSectionSingleDetailWithPartner_whenGetAnnualTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = new ApiAssessmentSectionSummary()
                .withAssessmentDetails(
                        List.of(new ApiAssessmentDetail()
                                .withPartnerAmount(BigDecimal.TEN)
                                .withApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY)
                        )
                );

        BigDecimal annualTotal = meansAssessmentService.getAnnualTotal(CaseType.EITHER_WAY, assessmentCriteria, List.of(section));
        assertThat(annualTotal).isEqualTo(
                TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                ).add(BigDecimal.TEN.multiply(
                                BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                        )
                )
        );
    }

    @Test
    public void givenSingleSectionTwoDetailWithPartner_whenGetAnnualTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = TestModelDataBuilder.getApiAssessmentSectionSummary();
        section.getAssessmentDetails().add(
                new ApiAssessmentDetail()
                        .withApplicantAmount(BigDecimal.TEN)
                        .withApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY)
                        .withPartnerAmount(BigDecimal.TEN)
                        .withPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY)
        );

        BigDecimal annualTotal = meansAssessmentService.getAnnualTotal(CaseType.EITHER_WAY, assessmentCriteria, List.of(section));
        assertThat(annualTotal).isEqualTo(
                TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                ).add(BigDecimal.valueOf(20).multiply(
                                BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                        )
                )
        );
    }

    @Test
    public void givenTwoSectionTwoDetailNoPartner_whenGetAnnualTotalIsInvoked_thenCorrectTotalIsCalculated() {
        List<ApiAssessmentSectionSummary> summaries = TestModelDataBuilder.getAssessmentSummaries();
        BigDecimal annualTotal = meansAssessmentService.getAnnualTotal(CaseType.EITHER_WAY, assessmentCriteria, summaries);
        BigDecimal expected = TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                BigDecimal.valueOf(
                        TestModelDataBuilder.TEST_FREQUENCY.getWeighting()
                ).multiply(BigDecimal.valueOf(2)));
        assertThat(annualTotal).isEqualTo(expected);
    }

    @Test
    public void givenTwoSectionTwoDetailWithPartner_whenGetAnnualTotalIsInvoked_thenCorrectTotalIsCalculated() {
        List<ApiAssessmentSectionSummary> summaries = TestModelDataBuilder.getAssessmentSummaries();
        List<ApiAssessmentDetail> section = summaries.get(0).getAssessmentDetails();
        section.get(0).setPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY);
        section.get(0).setPartnerAmount(TestModelDataBuilder.TEST_APPLICANT_VALUE);
        BigDecimal annualTotal = meansAssessmentService.getAnnualTotal(CaseType.EITHER_WAY, assessmentCriteria, summaries);
        BigDecimal expected = TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                BigDecimal.valueOf(
                        TestModelDataBuilder.TEST_FREQUENCY.getWeighting()
                ).multiply(BigDecimal.valueOf(3)));
        assertThat(annualTotal).isEqualTo(expected);
    }

    @Test
    public void givenValidRequest_whenCreateInitialAssessmentIsInvoked_thenAssessmentIsPersisted() {
        when(assessmentCriteriaService.getAssessmentCriteria(any(LocalDateTime.class), anyBoolean(), anyBoolean())).thenReturn(
                TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails()
        );
        when(childWeightingService.getTotalChildWeighting(anyList(), any(AssessmentCriteriaEntity.class))).thenReturn(
                BigDecimal.valueOf(0.85)
        );
        meansAssessmentService.createInitialAssessment(TestModelDataBuilder.getCreateMeansAssessmentRequest(true));
        verify(meansAssessmentService).persistAssessment(any(ApiCreateAssessment.class), anyString());
    }

    @Test
    public void givenPositiveAnnualTotal_whenGetAdjustedIncomeIsInvoked_thenAdjustedIncomeIsCalculated() {
        BigDecimal annualTotal = BigDecimal.valueOf(11000);
        BigDecimal totalChildWeighting = BigDecimal.valueOf(0.5);
        BigDecimal combinedWeightingFactor =
                TestModelDataBuilder.TEST_APPLICANT_WEIGHTING_FACTOR.add(
                        TestModelDataBuilder.TEST_PARTNER_WEIGHTING_FACTOR
                ).add(totalChildWeighting);

        BigDecimal expected = annualTotal.divide(combinedWeightingFactor, RoundingMode.UP)
                .setScale(2, RoundingMode.UP);

        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(totalChildWeighting);

        assertThat(meansAssessmentService.getAdjustedIncome(
                TestModelDataBuilder.getCreateMeansAssessmentRequest(true), assessmentCriteria, annualTotal)
        ).isEqualTo(expected);
    }

    @Test
    public void givenZeroAnnualTotal_whenGetAdjustedIncomeIsInvoked_thenReturnsZero() {
        BigDecimal annualTotal = BigDecimal.ZERO;
        when(childWeightingService.getTotalChildWeighting(
                anyList(), any(AssessmentCriteriaEntity.class))
        ).thenReturn(BigDecimal.ZERO);
        assertThat(meansAssessmentService.getAdjustedIncome(
                TestModelDataBuilder.getCreateMeansAssessmentRequest(true), assessmentCriteria, annualTotal)
        ).isEqualTo(BigDecimal.ZERO);
    }
}
