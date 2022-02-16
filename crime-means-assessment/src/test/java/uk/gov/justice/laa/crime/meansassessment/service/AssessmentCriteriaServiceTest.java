package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCriteriaServiceTest {

    private static final int VALID_ASSESSMENT_CRITERIA_ID = 1000;

    @InjectMocks
    private AssessmentCriteriaService assessmentCriteriaService;

    @Mock
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    private AssessmentCriteriaEntity assessmentCriteriaEntity;

    @Before
    public void setUp() {
        //given
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        assessmentCriteriaEntity.setId(VALID_ASSESSMENT_CRITERIA_ID);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenValidDateWithPartnerAndNoContraryInterestIsProvided_ThenAssessmentCriteriaShouldBeReturnedWithPartnerWeightingFactor() {
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(assessmentCriteriaEntity);
        // when Assessment Criteria valid after a certain time are requested
        AssessmentCriteriaEntity result = assessmentCriteriaService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), true, false);
        // then expected Assessment Criteria are returned
        assertEquals(assessmentCriteriaEntity.getPartnerWeightingFactor(), result.getPartnerWeightingFactor());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenValidDateAndNoPartnerIsProvided_ThenAssessmentCriteriaShouldBeReturnedWithZeroedPartnerWeightingFactor() {
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(assessmentCriteriaEntity);
        // when Assessment Criteria valid after a certain time are requested
        AssessmentCriteriaEntity result = assessmentCriteriaService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), false, false);
        // then expected Assessment Criteria are returned
        assertEquals(assessmentCriteriaEntity, result);
        assertEquals(result.getPartnerWeightingFactor(), BigDecimal.ZERO);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenValidDateWithPartnerAndContraryInterestIsProvided_ThenAssessmentCriteriaShouldBeReturnedWithPartnerWeightingFactor() {
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(assessmentCriteriaEntity);
        // when Assessment Criteria valid after a certain time are requested
        AssessmentCriteriaEntity result = assessmentCriteriaService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), true, true);
        // then expected Assessment Criteria are returned
        assertEquals(assessmentCriteriaEntity, result);
        assertEquals(result.getPartnerWeightingFactor(), assessmentCriteriaEntity.getPartnerWeightingFactor());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenValidDateWithoutPartnerAndContraryInterestIsProvided_ThenAssessmentCriteriaShouldBeReturnedWithZeroedPartnerWeightingFactor() {
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(assessmentCriteriaEntity);
        // when Assessment Criteria valid after a certain time are requested
        AssessmentCriteriaEntity result = assessmentCriteriaService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), false, false);
        // then expected Assessment Criteria are returned
        assertEquals(assessmentCriteriaEntity, result);
        assertEquals(result.getPartnerWeightingFactor(), BigDecimal.ZERO);
    }

    @Test(expected = AssessmentCriteriaNotFoundException.class)
    public void givenAssessmentCriteriaIsPopulated_WhenInvalidDateWithPartnerAndNoContraryInterestIsProvided_ThenAssessmentCriteriaNotFoundExceptionIsThrown() throws AssessmentCriteriaNotFoundException {
        // given Assessment Criteria is populated and no results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(null);
        // when Assessment Criteria with invalid date are requested
        AssessmentCriteriaEntity result = assessmentCriteriaService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.minusYears(100), true, true);
    }
}