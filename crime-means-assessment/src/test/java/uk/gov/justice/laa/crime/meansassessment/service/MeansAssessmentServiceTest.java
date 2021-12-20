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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentServiceTest {
    private static final long VALID_ASSESSMENT_CRITERIA_ID = 10000l;
    private static final long INVALID_ASSESSMENT_CRITERIA_ID = 20000l;

    @InjectMocks
    private MeansAssessmentService meansAssessmentService;

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
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateWithPartnerAndNoContraryInterestIsProvidedThenAssessmentCriteriaShouldBeReturnedWithPartnerWeightingFactor(){
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(Arrays.asList(assessmentCriteriaEntity));
        // when Assessment Criteria valid after a certain time are requested
        try {
            List<AssessmentCriteriaEntity> results = meansAssessmentService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), true, false);
            // then expected Assessment Criteria are returned
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals(assessmentCriteriaEntity, results.get(0));
            assertEquals(assessmentCriteriaEntity.getPartnerWeightingFactor(), results.get(0).getPartnerWeightingFactor());
        } catch (Exception e) {
            fail("Unexpected exception : "+ e.getMessage());
        }
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateAndNoPartnerIsProvidedThenAssessmentCriteriaShouldBeReturnedWithoutPartnerWeightingFactor(){
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(Arrays.asList(assessmentCriteriaEntity));
        // when Assessment Criteria valid after a certain time are requested
        try {
            List<AssessmentCriteriaEntity> results = meansAssessmentService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), false, false);
            // then expected Assessment Criteria are returned
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals(assessmentCriteriaEntity, results.get(0));
            assertNull(results.get(0).getPartnerWeightingFactor());
        } catch (Exception e) {
            fail("Unexpected exception : "+ e.getMessage());
        }
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateWithPartnerAndContraryInterestIsProvidedThenAssessmentCriteriaShouldBeReturnedWithoutPartnerWeightingFactor(){
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(Arrays.asList(assessmentCriteriaEntity));
        // when Assessment Criteria valid after a certain time are requested
        try {
            List<AssessmentCriteriaEntity> results = meansAssessmentService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), true, true);
            // then expected Assessment Criteria are returned
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals(assessmentCriteriaEntity, results.get(0));
            assertNull(results.get(0).getPartnerWeightingFactor());
        } catch (Exception e) {
            fail("Unexpected exception : "+ e.getMessage());
        }
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateWithoutPartnerAndContraryInterestIsProvidedThenAssessmentCriteriaShouldBeReturnedWithoutPartnerWeightingFactor(){
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(Arrays.asList(assessmentCriteriaEntity));
        // when Assessment Criteria valid after a certain time are requested
        try {
            List<AssessmentCriteriaEntity> results = meansAssessmentService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1), false, false);
            // then expected Assessment Criteria are returned
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals(assessmentCriteriaEntity, results.get(0));
            assertNull(results.get(0).getPartnerWeightingFactor());
        } catch (Exception e) {
            fail("Unexpected exception : "+ e.getMessage());
        }
    }

    @Test(expected = AssessmentCriteriaNotFoundException.class)
    public void givenAssessmentCriteriaIsPopulatedWhenInvalidDateWithPartnerAndNoContraryInterestIsProvidedThenAssessmentCriteriaNotFoundExceptionIsThrown() throws AssessmentCriteriaNotFoundException {
        // given Assessment Criteria is populated and no results are returned
        when(assessmentCriteriaRepository.findAssessmentCriteriaForDate(any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        // when Assessment Criteria with invalid date are requested
        List<AssessmentCriteriaEntity> results = meansAssessmentService.getAssessmentCriteria(TestModelDataBuilder.TEST_DATE_FROM.minusYears(100), true, true);
    }
}