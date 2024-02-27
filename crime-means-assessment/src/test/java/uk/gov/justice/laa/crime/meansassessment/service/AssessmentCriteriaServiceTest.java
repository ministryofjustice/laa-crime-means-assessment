package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.CaseTypeAssessmentCriteriaDetailValueEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailFrequencyRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.CaseTypeAssessmentCriteriaDetailValueRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentCriteriaServiceTest {

    private static final int VALID_ASSESSMENT_CRITERIA_ID = 1000;
    private AssessmentCriteriaEntity assessmentCriteriaEntity;

    @Spy
    @InjectMocks
    private AssessmentCriteriaService assessmentCriteriaService;

    @Mock
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    @Mock
    private AssessmentCriteriaDetailFrequencyRepository assessmentCriteriaDetailFrequencyRepository;

    @Mock
    private CaseTypeAssessmentCriteriaDetailValueRepository caseTypeAssessmentCriteriaDetailValueRepository;

    @BeforeEach
    void setUp() {
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();
        assessmentCriteriaEntity.setId(VALID_ASSESSMENT_CRITERIA_ID);
    }

    @Test
    void givenValidDateWithPartnerAndNoContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithPartnerWeightingFactor() {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class)))
                .thenReturn(assessmentCriteriaEntity);
        AssessmentCriteriaEntity result =
                assessmentCriteriaService.getAssessmentCriteria(
                        TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), true, false);
        assertThat(assessmentCriteriaEntity.getPartnerWeightingFactor()).isEqualTo(result.getPartnerWeightingFactor());
    }

    @Test
    void givenValidDateAndNoPartner_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithZeroedPartnerWeightingFactor() {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class)))
                .thenReturn(assessmentCriteriaEntity);

        AssessmentCriteriaEntity result =
                assessmentCriteriaService.getAssessmentCriteria(
                        TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), false, false
                );
        assertThat(assessmentCriteriaEntity).isEqualTo(result);
        assertThat(BigDecimal.ZERO).isEqualTo(result.getPartnerWeightingFactor());
    }

    @Test
    void givenValidDateWithPartnerAndContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithPartnerWeightingFactor() {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class)))
                .thenReturn(assessmentCriteriaEntity);

        AssessmentCriteriaEntity result =
                assessmentCriteriaService.getAssessmentCriteria(
                        TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), true, true
                );
        assertThat(assessmentCriteriaEntity).isEqualTo(result);
        assertThat(result.getPartnerWeightingFactor()).isEqualTo(assessmentCriteriaEntity.getPartnerWeightingFactor());
    }

    @Test
    void givenValidDateWithoutPartnerAndContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithZeroedPartnerWeightingFactor() {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class)))
                .thenReturn(assessmentCriteriaEntity);

        AssessmentCriteriaEntity result =
                assessmentCriteriaService.getAssessmentCriteria(
                        TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), false, false
                );
        assertThat(assessmentCriteriaEntity).isEqualTo(result);
        assertThat(BigDecimal.ZERO).isEqualTo(result.getPartnerWeightingFactor());
    }

    @Test
    void givenInvalidDateWithPartnerAndNoContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenExceptionIsThrown() throws AssessmentCriteriaNotFoundException {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(null);

        LocalDateTime criteriaDate = TestModelDataBuilder.TEST_DATE_FROM.minusYears(100);

        assertThatThrownBy(
                () -> assessmentCriteriaService.getAssessmentCriteria(criteriaDate, true, true)
        ).isInstanceOf(AssessmentCriteriaNotFoundException.class)
                .hasMessageContaining("No Assessment Criteria found for date " + criteriaDate);
    }

    @Test
    void givenValidFrequency_whenCheckCriteriaDetailFrequencyIsInvoked_thenDoesNothing() {
        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity()));
        AssessmentCriteriaDetailEntity detail = TestModelDataBuilder.getAssessmentCriteriaDetailEntity();
        assessmentCriteriaService.checkCriteriaDetailFrequency(detail, Frequency.WEEKLY);
    }

    @Test
    void givenInvalidFrequency_whenCheckCriteriaDetailFrequencyIsInvoked_thenExceptionIsThrown() {
        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.empty());
        AssessmentCriteriaDetailEntity detail = TestModelDataBuilder.getAssessmentCriteriaDetailEntity();
        assertThatThrownBy(
                () -> assessmentCriteriaService.checkCriteriaDetailFrequency(detail, Frequency.WEEKLY)
        ).isInstanceOf(ValidationException.class)
                .hasMessageContaining("Frequency: WEEKLY not valid for: " + detail.getDescription());
    }

    @Test
    void givenDetailWithIncorrectId_whenCheckAssessmentDetailIsInvoked_thenExceptionIsThrown() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails().get(0);
        detail.setCriteriaDetailId(0);

        assertThatThrownBy(() -> assessmentCriteriaService.checkAssessmentDetail(
                        CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail
                )
        ).isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format(
                        "Section: %s criteria detail item: %d does not exist for criteria id: %s",
                        section,
                        detail.getCriteriaDetailId(),
                        assessmentCriteriaEntity.getId()
                ));
    }

    @Test
    void givenDetailWithIncorrectSection_whenCheckAssessmentDetailIsInvoked_thenExceptionIsThrown() {
        String section = "BAD_SECTION";
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails().get(0);

        assertThatThrownBy(
                () -> assessmentCriteriaService.checkAssessmentDetail(
                        CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail
                )
        ).isInstanceOf(ValidationException.class)
                .hasMessageContaining(String.format(
                                "Section: %s criteria detail item: %d does not exist for criteria id: %s",
                                section,
                                detail.getCriteriaDetailId(),
                                assessmentCriteriaEntity.getId()
                        )
                );
    }

    @Test
    void givenApplicantFrequency_whenCheckAssessmentDetailIsInvoked_thenValidateApplicantFrequency() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails().get(0);

        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity()));

        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
    }

    @Test
    void givenPartnerFrequency_whenCheckAssessmentDetailIsInvoked_thenValidatePartnerFrequency() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        detail.setApplicantFrequency(null);
        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity()));

        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
    }

    @Test
    void givenAppealCostsCriteriaDetailIsNull_whenCheckAssessmentDetailIsInvoked_thenDoNothing() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        when(caseTypeAssessmentCriteriaDetailValueRepository.findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class))
        ).thenReturn(Optional.empty());

        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity()));

        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
    }

    @Test
    void givenAppealCostsCriteriaDetailWithCorrectAmounts_whenCheckAssessmentDetailIsInvoked_thenDoNothing() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        when(caseTypeAssessmentCriteriaDetailValueRepository.findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class))
        ).thenReturn(Optional.of(TestModelDataBuilder.getCaseTypeAssessmentCriteriaDetailValueEntity()));

        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity()));

        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
    }

    @Test
    void givenAppealCostsCriteriaDetailWithIncorrectAmounts_whenCheckAssessmentDetailIsInvoked_thenThrowsException() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        CaseTypeAssessmentCriteriaDetailValueEntity criteriaDetailValueEntity =
                TestModelDataBuilder.getCaseTypeAssessmentCriteriaDetailValueEntity();
        when(caseTypeAssessmentCriteriaDetailValueRepository.findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class))
        ).thenReturn(Optional.of(criteriaDetailValueEntity));

        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity()));

        String expectedErrorMessage = "Incorrect amount entered for: " + TestModelDataBuilder.TEST_DESCRIPTION;
        ThrowableAssert.ThrowingCallable function =
                () -> assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);

        SoftAssertions.assertSoftly(softly -> {

            detail.setApplicantAmount(BigDecimal.ZERO);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setApplicantAmount(TestModelDataBuilder.TEST_APPLICANT_VALUE);

            detail.setApplicantFrequency(null);
            assertThatNoException().isThrownBy(function);
            detail.setApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY);

            detail.setApplicantFrequency(Frequency.FOUR_WEEKLY);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY);

            detail.setPartnerAmount(BigDecimal.ZERO);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setPartnerAmount(TestModelDataBuilder.TEST_PARTNER_VALUE);

            detail.setPartnerFrequency(null);
            assertThatNoException().isThrownBy(function);
            detail.setPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY);

            detail.setPartnerFrequency(Frequency.FOUR_WEEKLY);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY);
        });
    }

    @Test
    void givenValidFrequency_whenGetFullAssessmentThresholdIsInvoked_thenThenAssessmentCriteriaDTOShouldBeReturned() {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class)))
                .thenReturn(TestModelDataBuilder.getAssessmentCriteriaEntity());
        BigDecimal result = assessmentCriteriaService.getFullAssessmentThreshold(TestModelDataBuilder.TEST_DATE_STRING);
        assertThat(result).isEqualTo(TestModelDataBuilder.TEST_FULL_THRESHOLD);
    }
}