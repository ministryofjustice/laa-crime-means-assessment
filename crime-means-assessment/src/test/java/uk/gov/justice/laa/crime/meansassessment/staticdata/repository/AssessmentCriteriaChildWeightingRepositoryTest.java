package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;

import java.util.Optional;

import static org.junit.Assert.*;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AssessmentCriteriaChildWeightingRepositoryTest {

    @Autowired
    private AssessmentCriteriaChildWeightingRepository assessmentCriteriaChildWeightingRepository;

    private AssessmentCriteriaChildWeightingEntity assessmentCriteriaChildWeightingEntity;

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenAllRecordsAreRequestThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting has been populated by Liquibase
        // when all records are requested
        Iterable<AssessmentCriteriaChildWeightingEntity> results = assessmentCriteriaChildWeightingRepository.findAll();
        // then at least one record is returned
        assertTrue(IterableUtils.size(results) > 0);
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenUnknownIdIsProvidedThenAssessmentCriteriaChildWeightingIsnNotReturned(){
        // given Assessment Criteria Child Weighting has been populated by Liquibase
        // when unknown id is provided
        Optional<AssessmentCriteriaChildWeightingEntity> result = assessmentCriteriaChildWeightingRepository.findById(1000000000L);
        // then no result is returned
        assertTrue(result.isEmpty());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenLookedUpOnDBThenItWillHaveAssessmentCriteriaObjectPopulated(){
        // given Assessment Criteria Child Weighting has been populated by Liquibase
        // when unknown id is provided
        Optional<AssessmentCriteriaChildWeightingEntity> result = assessmentCriteriaChildWeightingRepository.findById(1L);
        // then result is returned
        assertFalse(result.isEmpty());
        var assessmentCriteriaChildWeightingReturned = result.get();
        assertNotNull(assessmentCriteriaChildWeightingReturned.getAssessmentCriteria());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenCorrectIdIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting record with given id is available
        assessmentCriteriaChildWeightingEntity = TestModelDataBuilder.getAssessmentCriteriaChildWeightingEntity();
        var savedAssessmentCriteriaChildWeightingEntity = assessmentCriteriaChildWeightingRepository.save(assessmentCriteriaChildWeightingEntity);

        // when Assessment Criteria Child Weighting is requested by id
        Optional<AssessmentCriteriaChildWeightingEntity> result = assessmentCriteriaChildWeightingRepository.findById(savedAssessmentCriteriaChildWeightingEntity.getId());

        // then correct Assessment Criteria Child Weighting is returned
        assertTrue(result.isPresent());
        var assessmentCriteriaChildWeightingEntityResult = result.get();
        assertEquals(assessmentCriteriaChildWeightingEntity, assessmentCriteriaChildWeightingEntityResult);
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenLowerAgeRangeFromIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting
        assessmentCriteriaChildWeightingRepository.save(TestModelDataBuilder.getAssessmentCriteriaChildWeightingEntity());

        // when valid Lower AgeRange is given
        var result = assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqual(TEST_INITIAL_LOWER_AGE_RANGE);
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenUpperAgeRangeFromIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting
        assessmentCriteriaChildWeightingRepository.save(TestModelDataBuilder.getAssessmentCriteriaChildWeightingEntity());
        // when valid  Upper Age Range is given
        var result = assessmentCriteriaChildWeightingRepository.findByUpperAgeRangeIsLessThanEqual(TEST_INITIAL_UPPER_AGE_RANGE);
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenLowerAgeRangeAndUpperAgeRangeFromIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting
        assessmentCriteriaChildWeightingRepository.save(TestModelDataBuilder.getAssessmentCriteriaChildWeightingEntity());
        // when valid Lower Age Range and Upper Age Range is given
        var result = assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(TEST_INITIAL_LOWER_AGE_RANGE, TEST_INITIAL_UPPER_AGE_RANGE);
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenWeightFactorIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting
        assessmentCriteriaChildWeightingRepository.save(TestModelDataBuilder.getAssessmentCriteriaChildWeightingEntity());
        // when valid Weight factor is given
        var result = assessmentCriteriaChildWeightingRepository.findByWeightingFactor(TEST_WEIGHTING_FACTOR);
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @After
    public void tearDown() {
        if(assessmentCriteriaChildWeightingEntity != null) {
            assessmentCriteriaChildWeightingRepository.delete(assessmentCriteriaChildWeightingEntity);
            assessmentCriteriaChildWeightingEntity = null;
        }
    }
}