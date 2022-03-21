package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.builder.CreateInitialAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.InitialMeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateAssessment;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitialAssessmentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InitialMeansAssessmentServiceTest {

    private AssessmentCriteriaEntity assessmentCriteria;
    private final BigDecimal upperThreshold = TestModelDataBuilder.TEST_INITIAL_UPPER_THRESHOLD;
    private final BigDecimal lowerThreshold = TestModelDataBuilder.TEST_INITIAL_LOWER_THRESHOLD;

    @Spy
    @InjectMocks
    private InitialMeansAssessmentService meansAssessmentService;

    @Mock
    private AssessmentCriteriaService assessmentCriteriaService;

    @Mock
    private AssessmentCriteriaChildWeightingService childWeightingService;

    @Mock
    private CreateInitialAssessmentBuilder createInitialAssessmentBuilder;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Before
    public void setUp() {
        assessmentCriteria = TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();
    }

    @Test
    public void givenIncomeBelowLowerThreshold_whenGetAssessmentResultIsInvoked_thenResultIsPass() {
        BigDecimal adjustedIncome = lowerThreshold.subtract(BigDecimal.valueOf(0.01));
        InitialAssessmentResult result =
                meansAssessmentService.getAssessmentResult(adjustedIncome, assessmentCriteria, "FMA");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result).isEqualTo(InitialAssessmentResult.PASS);
        });
    }

    @Test
    public void givenIncomeBetweenThresholds_whenGetAssessmentResultIsInvoked_thenResultIsFull() {
        BigDecimal adjustedIncome = lowerThreshold.add(BigDecimal.valueOf(0.01));
        InitialAssessmentResult result =
                meansAssessmentService.getAssessmentResult(adjustedIncome, assessmentCriteria, "FMA");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result).isEqualTo(InitialAssessmentResult.FULL);
        });
    }

    @Test
    public void givenIncomeAboveUpperThreshold_whenGetAssessmentResultIsInvoked_thenResultIsFail() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        InitialAssessmentResult result =
                meansAssessmentService.getAssessmentResult(adjustedIncome, assessmentCriteria, "FMA");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result).isEqualTo(InitialAssessmentResult.FAIL);
        });
    }

    @Test
    public void givenIncomeAboveUpperThresholdAndHardshipApplication_whenGetAssessmentResultIsInvoked_thenResultIsHardship() {
        BigDecimal adjustedIncome = upperThreshold.add(BigDecimal.valueOf(0.01));
        InitialAssessmentResult result =
                meansAssessmentService.getAssessmentResult(adjustedIncome, assessmentCriteria, "HR");
        SoftAssertions.assertSoftly(softly -> {
            assertThat(result).isEqualTo(InitialAssessmentResult.HARDSHIP);
        });
    }

    @Test
    public void givenNullAmounts_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withPartnerAmount(null)
                .withApplicantAmount(null);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenZeroAmounts_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withPartnerAmount(BigDecimal.ZERO)
                .withApplicantAmount(BigDecimal.ZERO);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenValidAAmountsAndFrequencies_whenGetDetailTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withApplicantAmount(BigDecimal.TEN)
                .withApplicantFrequency(Frequency.MONTHLY)
                .withPartnerAmount(BigDecimal.ONE)
                .withPartnerFrequency(Frequency.MONTHLY);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail);
        assertThat(total).isEqualTo(BigDecimal.valueOf(132));
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
                        new ArrayList<>(
                                List.of(new ApiAssessmentDetail()
                                        .withPartnerAmount(BigDecimal.TEN)
                                        .withPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY)
                                )
                        )
                );

        BigDecimal annualTotal = meansAssessmentService.getAnnualTotal(CaseType.EITHER_WAY, assessmentCriteria, List.of(section));
        assertThat(annualTotal).isEqualTo(BigDecimal.valueOf(120));
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
        when(createInitialAssessmentBuilder.build(any(InitialMeansAssessmentDTO.class))).thenReturn(new ApiCreateAssessment());

        doReturn(new ApiCreateMeansAssessmentResponse()).when(maatCourtDataService).postMeansAssessment(any(ApiCreateAssessment.class), anyString());

        meansAssessmentService.createInitialAssessment(TestModelDataBuilder.getCreateMeansAssessmentRequest(true));
        verify(maatCourtDataService).postMeansAssessment(any(ApiCreateAssessment.class), anyString());
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
