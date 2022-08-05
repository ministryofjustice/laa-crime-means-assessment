package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.builder.MaatCourtDataAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentProcessingException;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentServiceTest {

    private final AssessmentCriteriaEntity assessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();

    private final MeansAssessmentRequestDTO meansAssessment =
            TestModelDataBuilder.getMeansAssessmentRequestDTO(true);

    private final MaatApiConfiguration.FinancialAssessmentEndpoints financialAssessmentEndpoints =
            new MaatApiConfiguration.FinancialAssessmentEndpoints();

    @Spy
    @InjectMocks
    private MeansAssessmentService meansAssessmentService;

    @Mock
    private AssessmentCriteriaService assessmentCriteriaService;

    @Mock
    private InitMeansAssessmentService initMeansAssessmentService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private FullMeansAssessmentService fullMeansAssessmentService;

    @Mock
    private MaatCourtDataAssessmentBuilder assessmentBuilder;

    @Mock
    private MeansAssessmentResponseBuilder meansAssessmentResponseBuilder;

    @Mock
    private MeansAssessmentServiceFactory meansAssessmentServiceFactory;


    @Mock
    private FullAssessmentAvailabilityService fullAssessmentAvailabilityService;


    @Before
    public void setup() {
        assessmentCriteria.setId(TestModelDataBuilder.TEST_CRITERIA_ID);
        financialAssessmentEndpoints.setCreateUrl("create-url");
        financialAssessmentEndpoints.setUpdateUrl("update-url");
    }

    @AfterEach
    public void resetMeansAssessment() {
        meansAssessment.setAssessmentStatus(TestModelDataBuilder.TEST_ASSESSMENT_STATUS);
        meansAssessment.setSectionSummaries(TestModelDataBuilder.getApiAssessmentSummaries(true));
    }

    @Test
    public void givenNullAmountsAndUsePartnerIsFalse_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withApplicantAmount(null);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail, false);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenNullAmountsAndUsePartnerIsTrue_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withPartnerAmount(null);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail, true);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenZeroAmountsAndUsePartnerIsTrue_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withPartnerAmount(BigDecimal.ZERO);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail, true);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenZeroAmountsAndUsePartnerIsFalse_whenGetDetailTotalIsInvoked_thenTotalIsZero() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withApplicantAmount(BigDecimal.ZERO);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail, false);
        assertThat(total).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void givenValidAmountAndFrequencyAndUsePartnerIsFalse_whenGetDetailTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withApplicantAmount(BigDecimal.TEN)
                .withApplicantFrequency(Frequency.MONTHLY);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail, false);
        assertThat(total).isEqualTo(BigDecimal.valueOf(120));
    }

    @Test
    public void givenValidAmountAndFrequencyAndUsePartnerIsTrue_whenGetDetailTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentDetail detail = new ApiAssessmentDetail()
                .withPartnerAmount(BigDecimal.TEN)
                .withPartnerFrequency(Frequency.MONTHLY);
        BigDecimal total = meansAssessmentService.getDetailTotal(detail, true);
        assertThat(total).isEqualTo(BigDecimal.valueOf(120));
    }

    @Test
    public void givenSingleSectionSingleDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        BigDecimal annualTotal = meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        assertThat(annualTotal).isEqualTo(
                TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                )
        );
    }

    @Test
    public void givenSingleSectionTwoDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = meansAssessment.getSectionSummaries().get(0);
        section.getAssessmentDetails().add(
                new ApiAssessmentDetail()
                        .withApplicantAmount(BigDecimal.TEN)
                        .withApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY)
        );

        BigDecimal annualTotal = meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
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
    public void givenSingleSectionSingleDetailWithPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = new ApiAssessmentSectionSummary()
                .withAssessmentDetails(
                        new ArrayList<>(
                                List.of(new ApiAssessmentDetail()
                                        .withPartnerAmount(BigDecimal.TEN)
                                        .withPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY)
                                )
                        )
                );
        meansAssessment.setSectionSummaries(List.of(section));
        BigDecimal annualTotal = meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        assertThat(annualTotal).isEqualTo(BigDecimal.valueOf(120));
    }

    @Test
    public void givenSingleSectionTwoDetailWithPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = meansAssessment.getSectionSummaries().get(0);
        section.getAssessmentDetails().add(
                new ApiAssessmentDetail()
                        .withApplicantAmount(BigDecimal.TEN)
                        .withApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY)
                        .withPartnerAmount(BigDecimal.TEN)
                        .withPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY)
        );

        BigDecimal annualTotal = meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
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
    public void givenTwoSectionTwoDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        meansAssessment.setSectionSummaries(TestModelDataBuilder.getAssessmentSummaries());
        BigDecimal annualTotal = meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        BigDecimal expected = TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                BigDecimal.valueOf(
                        TestModelDataBuilder.TEST_FREQUENCY.getWeighting()
                ).multiply(BigDecimal.valueOf(2)));
        assertThat(annualTotal).isEqualTo(expected);
    }

    @Test
    public void givenTwoSectionTwoDetailWithPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        meansAssessment.setSectionSummaries(TestModelDataBuilder.getAssessmentSummaries());

        List<ApiAssessmentDetail> section =
                meansAssessment.getSectionSummaries().get(0).getAssessmentDetails();
        section.get(0).setPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY);
        section.get(0).setPartnerAmount(TestModelDataBuilder.TEST_APPLICANT_VALUE);

        BigDecimal summariesTotal =
                meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);

        BigDecimal expected = TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                BigDecimal.valueOf(
                        TestModelDataBuilder.TEST_FREQUENCY.getWeighting()
                ).multiply(BigDecimal.valueOf(3)));

        assertThat(summariesTotal).isEqualTo(expected);
    }

    private void setupDoAssessmentStubbing(AssessmentType assessmentType) {
        when(assessmentCriteriaService.getAssessmentCriteria(any(LocalDateTime.class), anyBoolean(), anyBoolean()))
                .thenReturn(assessmentCriteria);

        when(assessmentBuilder.build(any(MeansAssessmentDTO.class), any(AssessmentRequestType.class)))
                .thenReturn(new MaatApiAssessmentRequest());

        when(meansAssessmentServiceFactory.getService(any(AssessmentType.class)))
                .thenReturn(
                        (AssessmentType.INIT.equals(assessmentType)) ? initMeansAssessmentService : fullMeansAssessmentService
                );

        when(initMeansAssessmentService.execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class))
        ).thenReturn(TestModelDataBuilder.getMeansAssessmentDTO());


        when(fullMeansAssessmentService.execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class))
        ).thenReturn(TestModelDataBuilder.getMeansAssessmentDTO());

        MaatApiAssessmentResponse maatApiAssessmentResponse =
                new MaatApiAssessmentResponse()
                        .withId(TestModelDataBuilder.MEANS_ASSESSMENT_ID)
                        .withInitTotAggregatedIncome(TestModelDataBuilder.TEST_AGGREGATED_INCOME)
                        .withInitResult(InitAssessmentResult.PASS.getResult())
                        .withInitResultReason(InitAssessmentResult.PASS.getReason())
                        .withInitAdjustedIncomeValue(TestModelDataBuilder.TEST_ADJUSTED_INCOME)
                        .withFassInitStatus(TestModelDataBuilder.TEST_ASSESSMENT_STATUS.getStatus());

        when(maatCourtDataService.postMeansAssessment(
                any(MaatApiAssessmentRequest.class), anyString(), any(AssessmentRequestType.class))
        ).thenReturn(maatApiAssessmentResponse);

    }

    @Test
    public void givenInitAssessmentType_whenDoAssessmentIsInvoked_thenCreateAssessmentIsPerformed() {
        setupDoAssessmentStubbing(AssessmentType.INIT);
        ApiMeansAssessmentResponse result = meansAssessmentService.doAssessment(meansAssessment, AssessmentRequestType.CREATE);

        verify(initMeansAssessmentService).execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class)
        );
        verify(fullAssessmentAvailabilityService).processFullAssessmentAvailable(meansAssessment, result);
        verify(meansAssessmentResponseBuilder).build(
                anyInt(), any(AssessmentCriteriaEntity.class), any(MeansAssessmentDTO.class)
        );
    }

    @Test
    public void givenFullAssessmentType_whenDoAssessmentIsInvoked_thenFullAssessmentIsPerformed() {
        setupDoAssessmentStubbing(AssessmentType.FULL);

        meansAssessment.setAssessmentType(AssessmentType.FULL);
        ApiMeansAssessmentResponse result = meansAssessmentService.doAssessment(meansAssessment, AssessmentRequestType.UPDATE);

        verify(fullMeansAssessmentService).execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class)
        );
        verify(fullAssessmentAvailabilityService).processFullAssessmentAvailable(meansAssessment, result);
        verify(meansAssessmentResponseBuilder).build(
                anyInt(), any(AssessmentCriteriaEntity.class), any(MeansAssessmentDTO.class)
        );
    }

    @Test
    public void givenUnexpectedFailure_whenDoAssessmentIsInvoked_thenAssessmentProcessingExceptionIsThrown() {

        doThrow(new RuntimeException()).when(assessmentCriteriaService).getAssessmentCriteria(
                any(LocalDateTime.class), anyBoolean(), anyBoolean()
        );

        assertThatThrownBy(
                () -> meansAssessmentService.doAssessment(meansAssessment, AssessmentRequestType.CREATE)
        ).isInstanceOf(AssessmentProcessingException.class).hasMessageContaining(
                "An error occurred whilst processing the assessment request with RepID: " + meansAssessment.getRepId()
        );
    }


}
