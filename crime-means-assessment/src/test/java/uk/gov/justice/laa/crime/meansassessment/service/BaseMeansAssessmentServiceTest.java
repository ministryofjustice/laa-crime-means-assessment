package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class BaseMeansAssessmentServiceTest {

    static final BigDecimal EXPECTED_TOTAL_AMOUNT = new BigDecimal("120.00");
    private final AssessmentCriteriaEntity assessmentCriteria =
            TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();
    @Mock
    private AssessmentCriteriaService assessmentCriteriaService;
    private MockConcreteClass mockAssessmentService;
    private MeansAssessmentRequestDTO meansAssessment;

    @BeforeEach
    void setup() {
        mockAssessmentService = new MockConcreteClass(assessmentCriteriaService);
        meansAssessment = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
    }

    @Test
    void givenNullAmount_whenCalculateDetailTotalIsInvoked_thenTotalIsZero() {
        BigDecimal total = mockAssessmentService.calculateDetailTotal(null, Frequency.FOUR_WEEKLY);
        assertThat(total).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void givenFrequencyIsNull_whenCalculateDetailTotalIsInvoked_thenTotalIsZero() {
        BigDecimal total = mockAssessmentService.calculateDetailTotal(BigDecimal.TEN, null);
        assertThat(total).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void givenZeroAmount_whenCalculateDetailTotalIsInvoked_thenTotalIsZero() {
        BigDecimal total = mockAssessmentService.calculateDetailTotal(BigDecimal.ZERO, Frequency.FOUR_WEEKLY);
        assertThat(total).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void givenValidAmountAndFrequency_whenCalculateDetailTotalIsInvoked_thenCorrectTotalIsCalculated() {
        BigDecimal total = mockAssessmentService.calculateDetailTotal(BigDecimal.TEN, Frequency.MONTHLY);
        assertThat(total).isEqualTo(EXPECTED_TOTAL_AMOUNT);
    }

    @Test
    void givenSingleSectionSingleDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        BigDecimal annualTotal = mockAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        assertThat(annualTotal).isEqualTo(
                TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(TestModelDataBuilder.TEST_FREQUENCY.getWeighting())
                ).setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Test
    void givenSingleSectionTwoDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = meansAssessment.getSectionSummaries().get(0);
        section.getAssessmentDetails().add(
                new ApiAssessmentDetail()
                        .withApplicantAmount(BigDecimal.TEN)
                        .withApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY)
        );

        BigDecimal annualTotal = mockAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
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
    void givenSingleSectionSingleDetailWithPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
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
        BigDecimal annualTotal = mockAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        assertThat(annualTotal).isEqualTo(EXPECTED_TOTAL_AMOUNT);
    }

    @Test
    void givenSingleSectionTwoDetailWithPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        ApiAssessmentSectionSummary section = meansAssessment.getSectionSummaries().get(0);
        section.getAssessmentDetails().add(
                new ApiAssessmentDetail()
                        .withApplicantAmount(BigDecimal.TEN)
                        .withApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY)
                        .withPartnerAmount(BigDecimal.TEN)
                        .withPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY)
        );

        BigDecimal annualTotal = mockAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
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
    void givenTwoSectionTwoDetailNoPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        meansAssessment.setSectionSummaries(TestModelDataBuilder.getAssessmentSummaries());
        BigDecimal annualTotal = mockAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);
        BigDecimal expected = TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                BigDecimal.valueOf(
                        TestModelDataBuilder.TEST_FREQUENCY.getWeighting()
                ).multiply(BigDecimal.valueOf(2))).setScale(2, RoundingMode.HALF_UP);
        assertThat(annualTotal).isEqualTo(expected);
    }

    @Test
    void givenTwoSectionTwoDetailWithPartner_whenCalculateSummariesTotalIsInvoked_thenCorrectTotalIsCalculated() {
        meansAssessment.setSectionSummaries(TestModelDataBuilder.getAssessmentSummaries());

        List<ApiAssessmentDetail> section =
                meansAssessment.getSectionSummaries().get(0).getAssessmentDetails();
        section.get(0).setPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY);
        section.get(0).setPartnerAmount(TestModelDataBuilder.TEST_APPLICANT_VALUE);

        BigDecimal summariesTotal =
                mockAssessmentService.calculateSummariesTotal(meansAssessment, assessmentCriteria);

        BigDecimal expected = TestModelDataBuilder.TEST_APPLICANT_VALUE.multiply(
                        BigDecimal.valueOf(
                                TestModelDataBuilder.TEST_FREQUENCY.getWeighting()
                        ).multiply(BigDecimal.valueOf(3)))
                .setScale(2, RoundingMode.HALF_UP);

        assertThat(summariesTotal).isEqualTo(expected);
    }

    private class MockConcreteClass extends BaseMeansAssessmentService {
        MockConcreteClass(AssessmentCriteriaService assessmentCriteriaService) {
            super(assessmentCriteriaService);
        }
    }

}
