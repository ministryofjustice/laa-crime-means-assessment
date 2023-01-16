package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.builder.MaatCourtDataAssessmentBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentResponseBuilder;
import uk.gov.justice.laa.crime.meansassessment.builder.MeansAssessmentSectionSummaryBuilder;
import uk.gov.justice.laa.crime.meansassessment.config.FeaturesConfiguration;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinAssIncomeEvidenceDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentProcessingException;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentServiceTest {

    public static final BigDecimal EXPECTED_TOTAL_AMOUNT = new BigDecimal("120.00");
    private final AssessmentCriteriaEntity assessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();

    private final MeansAssessmentRequestDTO meansAssessment =
            TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
    @Spy
    private final FeaturesConfiguration featuresConfiguration = new FeaturesConfiguration();
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
    private AssessmentCompletionService assessmentCompletionService;
    @Mock
    private FullAssessmentAvailabilityService fullAssessmentAvailabilityService;
    @Mock
    private AssessmentCriteriaDetailService assessmentCriteriaDetailService;
    @Mock
    private MeansAssessmentSectionSummaryBuilder meansAssessmentSectionSummaryBuilder;
    @Mock
    private IncomeEvidenceService incomeEvidenceService;

    @Before
    public void setup() {
        assessmentCriteria.setId(TestModelDataBuilder.TEST_CRITERIA_ID);
        featuresConfiguration.setDateCompletionEnabled(false);
    }

    @AfterEach
    public void resetMeansAssessment() {
        meansAssessment.setAssessmentStatus(TestModelDataBuilder.TEST_ASSESSMENT_STATUS);
        meansAssessment.setSectionSummaries(TestModelDataBuilder.getApiAssessmentSummaries(true));
    }

    @Test
    public void givenNullAmount_whenCalculateDetailTotalIsInvoked_thenTotalIsZero() {
        BigDecimal total = meansAssessmentService.calculateDetailTotal(null, Frequency.FOUR_WEEKLY);
        assertThat(total).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void givenFrequencyIsNull_whenCalculateDetailTotalIsInvoked_thenTotalIsZero() {
        BigDecimal total = meansAssessmentService.calculateDetailTotal(BigDecimal.TEN, null);
        assertThat(total).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void givenZeroAmount_whenCalculateDetailTotalIsInvoked_thenTotalIsZero() {
        BigDecimal total = meansAssessmentService.calculateDetailTotal(BigDecimal.ZERO, Frequency.FOUR_WEEKLY);
        assertThat(total).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void givenValidAmountAndFrequency_whenCalculateDetailTotalIsInvoked_thenCorrectTotalIsCalculated() {
        BigDecimal total = meansAssessmentService.calculateDetailTotal(BigDecimal.TEN, Frequency.MONTHLY);
        assertThat(total).isEqualTo(EXPECTED_TOTAL_AMOUNT);
    }

    @Test
    public void givenSingleSectionSingleDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        BigDecimal annualTotal = meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        assertThat(annualTotal).isEqualTo(
                TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                ).setScale(2, RoundingMode.HALF_UP)
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
                ).setScale(2, RoundingMode.HALF_UP)
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
        assertThat(annualTotal).isEqualTo(EXPECTED_TOTAL_AMOUNT);
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
                ).setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Test
    public void givenTwoSectionTwoDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        meansAssessment.setSectionSummaries(TestModelDataBuilder.getAssessmentSummaries());
        BigDecimal annualTotal = meansAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        BigDecimal expected = TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                BigDecimal.valueOf(
                        TestModelDataBuilder.TEST_FREQUENCY.getWeighting()
                ).multiply(BigDecimal.valueOf(2))).setScale(2, RoundingMode.HALF_UP);
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
                        ).multiply(BigDecimal.valueOf(3)))
                .setScale(2, RoundingMode.HALF_UP);

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

        when(maatCourtDataService.persistMeansAssessment(
                any(MaatApiAssessmentRequest.class), anyString(), any(AssessmentRequestType.class))
        ).thenReturn(maatApiAssessmentResponse);

    }

    //    TODO: Remove this test once the dateCompletion feature is enabled
    @Test
    public void givenDateCompletionFlagEnabled__whenDoAssessmentIsInvoked_thenAssessmentCompletionServiceIsCalled() {
        setupDoAssessmentStubbing(AssessmentType.INIT);
        featuresConfiguration.setDateCompletionEnabled(true);
        meansAssessmentService.doAssessment(meansAssessment, AssessmentRequestType.CREATE);
        verify(assessmentCompletionService).execute(any(MeansAssessmentDTO.class), anyString());
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
                any(MaatApiAssessmentResponse.class), any(AssessmentCriteriaEntity.class), any(MeansAssessmentDTO.class)
        );
    }

    @Test
    public void givenFullAssessmentType_whenDoAssessmentIsInvoked_thenFullAssessmentIsPerformed() {
        setupDoAssessmentStubbing(AssessmentType.FULL);

        meansAssessment.setAssessmentType(AssessmentType.FULL);
        meansAssessmentService.doAssessment(meansAssessment, AssessmentRequestType.UPDATE);

        verify(fullMeansAssessmentService).execute(
                any(BigDecimal.class), any(MeansAssessmentRequestDTO.class), any(AssessmentCriteriaEntity.class)
        );
        verify(meansAssessmentResponseBuilder).build(
                any(MaatApiAssessmentResponse.class), any(AssessmentCriteriaEntity.class), any(MeansAssessmentDTO.class)
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

    @Test
    public void givenMaatApiAssessmentResponse_whenUpdateDetailIdsIsInvoked_thenDetailIdsAreUpdated() {
        MeansAssessmentDTO meansAssessmentDTO = TestModelDataBuilder.getMeansAssessmentDTO();
        meansAssessmentService.updateDetailIds(
                meansAssessmentDTO, TestModelDataBuilder.getMaatApiAssessmentResponse()
        );
        assertThat(meansAssessmentDTO.getMeansAssessment().getSectionSummaries().get(0).getAssessmentDetails().get(0).getId())
                .isEqualTo(TestModelDataBuilder.getMaatApiAssessmentResponse().getAssessmentDetails().get(0).getId());
    }

    @Test
    public void givenEmptyAssessmentDetails_whenGetAssessmentSectionSummaryInvoked_thenReturnEmptyAssessmentSectionSummaryList() {
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList =
                meansAssessmentService.getAssessmentSectionSummary(TestModelDataBuilder.getFinancialAssessmentDTO());
        assertThat(true).isEqualTo(assessmentSectionSummaryList.isEmpty());
    }

    @Test
    public void givenEmptyAssessmentCriteriaDetail_whenGetAssessmentSectionSummaryInvoked_thenReturnEmptyAssessmentSectionSummaryList() {
        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList =
                meansAssessmentService.getAssessmentSectionSummary(
                        TestModelDataBuilder.getFinancialAssessmentDTOWithDetails()
                );
        assertThat(true).isEqualTo(assessmentSectionSummaryList.isEmpty());
    }

    @Test
    public void givenAssessmentDetails_whenGetAssessmentSectionSummaryInvoked_thenReturnAssessmentSectionSummaryList() {

        doReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailEntity(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA)))
                .when(assessmentCriteriaDetailService).getAssessmentCriteriaDetailById(any());
        when(meansAssessmentSectionSummaryBuilder.buildAssessmentDTO(any(), any())).thenReturn(TestModelDataBuilder
                .getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_TYPE_INIT, TestModelDataBuilder.TEST_SEQ));
        meansAssessmentService.getAssessmentSectionSummary(TestModelDataBuilder.getFinancialAssessmentDTOWithDetails());
        verify(meansAssessmentSectionSummaryBuilder, times(2)).buildAssessmentDTO(any(), any());
        verify(meansAssessmentSectionSummaryBuilder, times(1)).buildAssessmentSectionSummary(any());
    }

    @Test
    public void testGetAssessmentDTOInvoked_whenNonAssessmentCriteriaDetail_thenReturnEmpty() {
        doReturn(Optional.empty()).when(assessmentCriteriaDetailService).getAssessmentCriteriaDetailById(any());
        List<AssessmentDTO> assessmentDTOS = meansAssessmentService.getAssessmentDTO(TestModelDataBuilder.getAssessmentDetails());
        assertThat(0).isEqualTo(assessmentDTOS.size());
    }

    @Test
    public void testGetAssessmentDTOInvoked_whenAssessmentCriteriaDetailFound_thenReturnAssessment() {
        doReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailEntity(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA)))
                .when(assessmentCriteriaDetailService).getAssessmentCriteriaDetailById(any());
        when(meansAssessmentSectionSummaryBuilder.buildAssessmentDTO(any(), any())).thenReturn(TestModelDataBuilder
                .getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA, TestModelDataBuilder.TEST_SEQ));
        List<AssessmentDTO> assessmentDTOS = meansAssessmentService.getAssessmentDTO(TestModelDataBuilder.getAssessmentDetails());
        assertThat(2).isEqualTo(assessmentDTOS.size());
        assertThat(assessmentDTOS.get(0).getSection()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA);
    }

    @Test
    public void testSortAssessmentDetailInvoked_whenAssessmentFound_thenReturnSortedBySectionAndSequence() {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA,
                TestModelDataBuilder.TEST_SEQ + TestModelDataBuilder.TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITB,
                TestModelDataBuilder.TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA,
                TestModelDataBuilder.TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_FULLA,
                TestModelDataBuilder.TEST_SEQ + TestModelDataBuilder.TEST_SEQ));
        assessmentDTOList.add(TestModelDataBuilder.getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_FULLA, TestModelDataBuilder.TEST_SEQ));

        meansAssessmentService.sortAssessmentDetail(assessmentDTOList);

        assertThat(assessmentDTOList.get(0).getSection()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_FULLA);
        assertThat(assessmentDTOList.get(0).getSequence()).isEqualTo(TestModelDataBuilder.TEST_SEQ);
        assertThat(assessmentDTOList.get(1).getSection()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_FULLA);
        assertThat(assessmentDTOList.get(1).getSequence()).isEqualTo(TestModelDataBuilder.TEST_SEQ + TestModelDataBuilder.TEST_SEQ);
        assertThat(assessmentDTOList.get(2).getSection()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA);
        assertThat(assessmentDTOList.get(2).getSequence()).isEqualTo(TestModelDataBuilder.TEST_SEQ);
        assertThat(assessmentDTOList.get(3).getSection()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA);
        assertThat(assessmentDTOList.get(3).getSequence()).isEqualTo(TestModelDataBuilder.TEST_SEQ + TestModelDataBuilder.TEST_SEQ);
        assertThat(assessmentDTOList.get(4).getSection()).isEqualTo(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITB);
        assertThat(assessmentDTOList.get(4).getSequence()).isEqualTo(TestModelDataBuilder.TEST_SEQ);

    }

    @Test
    public void givenInvalidAssessmentId_whenGetOldAssessmentInvoked_thenReturnEmpty() {

        when(maatCourtDataService.getFinancialAssessment(any(), any())).thenReturn(null);
        ApiGetMeansAssessmentResponse apiMeansAssessmentResponse =
                meansAssessmentService.getOldAssessment(
                        TestModelDataBuilder.MEANS_ASSESSMENT_ID, TestModelDataBuilder.MEANS_ASSESSMENT_TRANSACTION_ID
                );
        verify(maatCourtDataService, times(1)).getFinancialAssessment(any(), any());
        assertThat(apiMeansAssessmentResponse).isNull();

    }

    @Test
    public void givenAssessmentId_whenGetOldAssessmentInvoked_thenReturnAssessment() {
        doReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailEntity(TestModelDataBuilder.TEST_ASSESSMENT_SECTION_INITA)))
                .when(assessmentCriteriaDetailService).getAssessmentCriteriaDetailById(any());
        when(meansAssessmentSectionSummaryBuilder.buildAssessmentDTO(any(), any())).thenReturn(TestModelDataBuilder
                .getAssessmentDTO(TestModelDataBuilder.TEST_ASSESSMENT_TYPE_INIT, TestModelDataBuilder.TEST_SEQ));
        when(maatCourtDataService.getFinancialAssessment(any(), any()))
                .thenReturn(TestModelDataBuilder.getFinancialAssessmentDTOWithDetails());
        meansAssessmentService.getOldAssessment(TestModelDataBuilder.MEANS_ASSESSMENT_ID, TestModelDataBuilder.MEANS_ASSESSMENT_TRANSACTION_ID);
        verify(maatCourtDataService, times(1)).getFinancialAssessment(any(), any());

    }

    @Test
    public void givenEmptyChildWeightings_whenMapChildWeightingsInvoked_thenResponseIsPopulatedWithEmptyChildWeightingsList() {
        ApiInitialMeansAssessment apiInitialMeansAssessment = new ApiInitialMeansAssessment();
        meansAssessmentService.mapChildWeightings(apiInitialMeansAssessment, TestModelDataBuilder
                .getFinancialAssessmentDTOWithDetails());
        assertThat(true).isEqualTo(apiInitialMeansAssessment.getChildWeighting().isEmpty());
    }

    @Test
    public void givenChildWeightings_whenMapChildWeightingsInvoked_thenResponseIsPopulatedWithChildWeightingsList() {
        ApiInitialMeansAssessment apiInitialMeansAssessment = new ApiInitialMeansAssessment();
        doReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaChildWeightingEntity()))
                .when(assessmentCriteriaService).getAssessmentCriteriaChildWeightingsById(any());
        meansAssessmentService.mapChildWeightings(apiInitialMeansAssessment, TestModelDataBuilder
                .getFinancialAssessmentDTOWithChildWeightings());
        assertThat(1).isEqualTo(apiInitialMeansAssessment.getChildWeighting().size());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingsEmpty_whenMapChildWeightingsInvoked_thenResponseIsPopulatedWithNoChildWeightingsList() {
        ApiInitialMeansAssessment apiInitialMeansAssessment = new ApiInitialMeansAssessment();
        meansAssessmentService.mapChildWeightings(apiInitialMeansAssessment, TestModelDataBuilder
                .getFinancialAssessmentDTOWithChildWeightings());
        assertThat(0).isEqualTo(apiInitialMeansAssessment.getChildWeighting().size());
    }

    @Test
    public void givenIncomeEvidenceList_whenSortFinAssIncomeEvidenceSummaryIsInvoked_thenReturnSortedList() {

        List<FinAssIncomeEvidenceDTO> finAssIncomeEvidenceDTOList = new ArrayList<>();
        finAssIncomeEvidenceDTOList.add(TestModelDataBuilder.getFinAssIncomeEvidenceDTO("N", "OTHER BUSINESS"));
        finAssIncomeEvidenceDTOList.add(TestModelDataBuilder.getFinAssIncomeEvidenceDTO("Y", "WAGE SLIP"));
        finAssIncomeEvidenceDTOList.add(TestModelDataBuilder.getFinAssIncomeEvidenceDTO("N", "BANK STATEMENT"));
        finAssIncomeEvidenceDTOList.add(TestModelDataBuilder.getFinAssIncomeEvidenceDTO("Y", "NINO"));
        finAssIncomeEvidenceDTOList.add(TestModelDataBuilder.getFinAssIncomeEvidenceDTO("N", "TAX RETURN"));

        meansAssessmentService.sortFinAssIncomeEvidenceSummary(finAssIncomeEvidenceDTOList);

        assertThat(finAssIncomeEvidenceDTOList.get(0).getMandatory()).isEqualTo("Y");
        assertThat(finAssIncomeEvidenceDTOList.get(0).getIncomeEvidence()).isEqualTo("WAGE SLIP");
        assertThat(finAssIncomeEvidenceDTOList.get(1).getMandatory()).isEqualTo("Y");
        assertThat(finAssIncomeEvidenceDTOList.get(1).getIncomeEvidence()).isEqualTo("NINO");
        assertThat(finAssIncomeEvidenceDTOList.get(2).getMandatory()).isEqualTo("N");
        assertThat(finAssIncomeEvidenceDTOList.get(2).getIncomeEvidence()).isEqualTo("TAX RETURN");
        assertThat(finAssIncomeEvidenceDTOList.get(3).getMandatory()).isEqualTo("N");
        assertThat(finAssIncomeEvidenceDTOList.get(3).getIncomeEvidence()).isEqualTo("OTHER BUSINESS");
        assertThat(finAssIncomeEvidenceDTOList.get(4).getMandatory()).isEqualTo("N");
        assertThat(finAssIncomeEvidenceDTOList.get(4).getIncomeEvidence()).isEqualTo("BANK STATEMENT");

    }

    @Test
    public void givenEmptyFinAssIncomeEvidence_whenMapIncomeEvidenceInvoked_thenResponseIsPopulatedWithEmptyIncomeEvidenceList() {
        ApiGetMeansAssessmentResponse apiGetMeansAssessmentResponse = new ApiGetMeansAssessmentResponse()
                .withIncomeEvidenceSummary(new ApiIncomeEvidenceSummary());
        meansAssessmentService.mapIncomeEvidence(apiGetMeansAssessmentResponse, TestModelDataBuilder
                .getFinancialAssessmentDTOWithDetails());
        assertThat(true).isEqualTo(apiGetMeansAssessmentResponse.getIncomeEvidenceSummary().getIncomeEvidence().isEmpty());
    }

    @Test
    public void givenIncomeEvidences_whenMapIncomeEvidenceInvoked_thenResponseIsPopulatedWithIncomeEvidenceList() {
        ApiGetMeansAssessmentResponse apiGetMeansAssessmentResponse = new ApiGetMeansAssessmentResponse()
                .withIncomeEvidenceSummary(new ApiIncomeEvidenceSummary());
        doReturn(Optional.of(TestModelDataBuilder.getIncomeEvidenceEntity()))
                .when(incomeEvidenceService).getIncomeEvidenceById(any());
        meansAssessmentService.mapIncomeEvidence(apiGetMeansAssessmentResponse, TestModelDataBuilder
                .getFinancialAssessmentDTOWithIncomeEvidence());
        assertThat(1).isEqualTo(apiGetMeansAssessmentResponse.getIncomeEvidenceSummary().getIncomeEvidence().size());
    }

    @Test
    public void givenInvalidEvidence_whenGetEvidenceTypeInvoked_thenEvidenceDescriptionIsNull() {
        String evidence = "NONE";
        ApiEvidenceType apiEvidenceType = meansAssessmentService.getEvidenceType(evidence);
        assertThat(evidence).isEqualTo(apiEvidenceType.getCode());
        assertThat(apiEvidenceType.getDescription()).isNull();
    }

    private void checkGenericResponseFields(ApiGetMeansAssessmentResponse response,
                                            FinancialAssessmentDTO financialAssessmentDTO) {
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.getId())
                    .isEqualTo(financialAssessmentDTO.getId());
            assertThat(response.getUsn())
                    .isEqualTo(financialAssessmentDTO.getUsn());
            assertThat(response.getInitialAssessment())
                    .isNotNull();
            assertThat(response.getFullAssessment())
                    .isNotNull();
            assertThat(response.getFullAvailable())
                    .isNotNull();
            assertThat(response.getIncomeEvidenceSummary())
                    .isNotNull();
        });
    }

    @Test
    public void givenInitAssessment_whenBuildMeansAssessmentResponseIsInvoked_thenBuildFullAssessmentResponse() {
        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO();
        meansAssessmentService.buildMeansAssessmentResponse(response, financialAssessmentDTO);

        verify(meansAssessmentSectionSummaryBuilder).buildInitialAssessment(
                any(ApiGetMeansAssessmentResponse.class),
                eq(financialAssessmentDTO),
                anyList(),
                ArgumentMatchers.any()
        );

        verify(meansAssessmentService)
                .mapChildWeightings(any(ApiInitialMeansAssessment.class), eq(financialAssessmentDTO));
        verify(meansAssessmentService)
                .mapIncomeEvidence(any(ApiGetMeansAssessmentResponse.class), eq(financialAssessmentDTO));

        checkGenericResponseFields(response, financialAssessmentDTO);
    }

    @Test
    public void givenFullAssessment_whenBuildMeansAssessmentResponseIsInvoked_thenBuildFullAssessmentResponse() {
        FinancialAssessmentDTO financialAssessmentDTO = TestModelDataBuilder.getFinancialAssessmentDTO();
        financialAssessmentDTO.setAssessmentType(AssessmentType.FULL.getType());
        financialAssessmentDTO.setFullAscrId(TestModelDataBuilder.TEST_CRITERIA_ID);
        financialAssessmentDTO.setFullAssessmentDate(TestModelDataBuilder.TEST_DATE_CREATED);

        ApiGetMeansAssessmentResponse response = new ApiGetMeansAssessmentResponse();
        meansAssessmentService.buildMeansAssessmentResponse(response, financialAssessmentDTO);

        verify(meansAssessmentSectionSummaryBuilder).buildInitialAssessment(
                any(ApiGetMeansAssessmentResponse.class),
                eq(financialAssessmentDTO),
                anyList(),
                ArgumentMatchers.any()
        );

        verify(meansAssessmentSectionSummaryBuilder).buildFullAssessment(
                any(ApiGetMeansAssessmentResponse.class),
                eq(financialAssessmentDTO),
                anyList(),
                ArgumentMatchers.any()
        );

        verify(meansAssessmentService)
                .mapChildWeightings(any(ApiInitialMeansAssessment.class), eq(financialAssessmentDTO));
        verify(meansAssessmentService)
                .mapIncomeEvidence(any(ApiGetMeansAssessmentResponse.class), eq(financialAssessmentDTO));

        checkGenericResponseFields(response, financialAssessmentDTO);
        assertThat(response.getFullAvailable()).isTrue();
    }

}
