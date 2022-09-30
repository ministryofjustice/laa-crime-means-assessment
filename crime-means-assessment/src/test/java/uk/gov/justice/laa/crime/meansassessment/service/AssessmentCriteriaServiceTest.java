package uk.gov.justice.laa.crime.meansassessment.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailFrequencyRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.CaseTypeAssessmentCriteriaDetailValueRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCriteriaServiceTest {

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

    @Before
    public void setUp() {
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntityWithDetails();
        assessmentCriteriaEntity.setId(VALID_ASSESSMENT_CRITERIA_ID);
    }

    @Test
    public void givenValidDateWithPartnerAndNoContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithPartnerWeightingFactor() {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class)))
                .thenReturn(assessmentCriteriaEntity);

        AssessmentCriteriaEntity result =
                assessmentCriteriaService.getAssessmentCriteria(
                        TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), true, false
                );
        assertThat(assessmentCriteriaEntity.getPartnerWeightingFactor()).isEqualTo(result.getPartnerWeightingFactor());
    }

    @Test
    public void givenValidDateAndNoPartner_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithZeroedPartnerWeightingFactor() {
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
    public void givenValidDateWithPartnerAndContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithPartnerWeightingFactor() {
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
    public void givenValidDateWithoutPartnerAndContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenAssessmentCriteriaShouldBeReturnedWithZeroedPartnerWeightingFactor() {
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
    public void givenInvalidDateWithPartnerAndNoContraryInterest_WhenGetAssessmentCriteriaIsInvoked_ThenExceptionIsThrown() throws AssessmentCriteriaNotFoundException {
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(null);

        LocalDateTime criteriaDate = TestModelDataBuilder.TEST_DATE_FROM.minusYears(100);

        assertThatThrownBy(
                () -> assessmentCriteriaService.getAssessmentCriteria(criteriaDate, true, true)
        ).isInstanceOf(AssessmentCriteriaNotFoundException.class)
                .hasMessageContaining("No Assessment Criteria found for date " + criteriaDate);
    }

    @Test
    public void givenValidFrequency_whenCheckFrequencyIsInvoked_thenDoesNothing() {
        when(assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(
                        any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
                )
        ).thenReturn(Optional.of(TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity()));
        AssessmentCriteriaDetailEntity detail = TestModelDataBuilder.getAssessmentCriteriaDetailEntity();
        assessmentCriteriaService.checkCriteriaDetailFrequency(detail, Frequency.WEEKLY);
    }

    @Test
    public void givenInvalidFrequency_whenCheckFrequencyIsInvoked_thenExceptionIsThrown() {
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
    public void givenDetailWithIncorrectId_whenCheckAssessmentDetailIsInvoked_thenExceptionIsThrown() {
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
    public void givenDetailWithIncorrectSection_whenCheckAssessmentDetailIsInvoked_thenExceptionIsThrown() {
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
    public void givenApplicantFrequency_whenCheckAssessmentDetailIsInvoked_thenValidateApplicantFrequency() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails().get(0);
        doNothing().when(assessmentCriteriaService)
                .checkCriteriaDetailFrequency(any(AssessmentCriteriaDetailEntity.class), any(Frequency.class));
        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
        verify(assessmentCriteriaService).checkCriteriaDetailFrequency(
                any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
        );
    }

    @Test
    public void givenPartnerFrequency_whenCheckAssessmentDetailIsInvoked_thenValidatePartnerFrequency() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        detail.setApplicantFrequency(null);

        doNothing().when(assessmentCriteriaService).checkCriteriaDetailFrequency(
                any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
        );
        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
        verify(assessmentCriteriaService).checkCriteriaDetailFrequency(
                any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
        );
    }

    @Test
    public void givenAppealCostsCriteriaDetailIsNull_whenCheckAssessmentDetailIsInvoked_thenDoNothing() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        when(caseTypeAssessmentCriteriaDetailValueRepository.findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class))
        ).thenReturn(Optional.empty());

        doNothing().when(assessmentCriteriaService).checkCriteriaDetailFrequency(
                any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
        );

        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
        verify(caseTypeAssessmentCriteriaDetailValueRepository).findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class)
        );
    }

    @Test
    public void givenAppealCostsCriteriaDetailWithCorrectAmounts_whenCheckAssessmentDetailIsInvoked_thenDoNothing() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        when(caseTypeAssessmentCriteriaDetailValueRepository.findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class))
        ).thenReturn(Optional.of(TestModelDataBuilder.getCaseTypeAssessmentCriteriaDetailValueEntity()));

        doNothing().when(assessmentCriteriaService).checkCriteriaDetailFrequency(
                any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
        );

        assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);
        verify(caseTypeAssessmentCriteriaDetailValueRepository).findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class)
        );
    }

    @Test
    public void givenAppealCostsCriteriaDetailWithIncorrectAmounts_whenCheckAssessmentDetailIsInvoked_thenThrowsException() {
        String section = TestModelDataBuilder.TEST_SECTION;
        ApiAssessmentDetail detail = TestModelDataBuilder.getApiAssessmentDetails(true).get(0);

        when(caseTypeAssessmentCriteriaDetailValueRepository.findByAssessmentCriteriaDetailAndCaseType(
                any(AssessmentCriteriaDetailEntity.class), any(CaseType.class))
        ).thenReturn(Optional.of(TestModelDataBuilder.getCaseTypeAssessmentCriteriaDetailValueEntity()));

        doNothing().when(assessmentCriteriaService).checkCriteriaDetailFrequency(
                any(AssessmentCriteriaDetailEntity.class), any(Frequency.class)
        );

        String expectedErrorMessage = "Incorrect amount entered for: " + TestModelDataBuilder.TEST_DESCRIPTION;
        ThrowableAssert.ThrowingCallable function =
                () -> assessmentCriteriaService.checkAssessmentDetail(CaseType.EITHER_WAY, section, assessmentCriteriaEntity, detail);

        SoftAssertions.assertSoftly(softly -> {
            detail.setApplicantAmount(BigDecimal.ZERO);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setApplicantAmount(TestModelDataBuilder.TEST_APPLICANT_VALUE);

            detail.setApplicantFrequency(Frequency.FOUR_WEEKLY);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setApplicantFrequency(TestModelDataBuilder.TEST_FREQUENCY);

            detail.setPartnerAmount(BigDecimal.ZERO);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setPartnerAmount(TestModelDataBuilder.TEST_PARTNER_VALUE);

            detail.setPartnerFrequency(Frequency.FOUR_WEEKLY);
            assertThatThrownBy(function).isInstanceOf(ValidationException.class).hasMessageContaining(expectedErrorMessage);
            detail.setPartnerFrequency(TestModelDataBuilder.TEST_FREQUENCY);
        });
    }
}