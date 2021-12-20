package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AssessmentCriteriaRepositoryTest {

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    private AssessmentCriteriaEntity assessmentCriteriaEntity;

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenAllRecordsAreRequestThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria has been populated by Liquibase
        // when all records are requested
        Iterable<AssessmentCriteriaEntity> results = assessmentCriteriaRepository.findAll();
        // then at least one record is returned
        assertTrue(IterableUtils.size(results) > 0);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenCorrectIdIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria record with given id is available
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentCriteriaEntity savedAssessmentCriteriaEntity = assessmentCriteriaRepository.save(assessmentCriteriaEntity);

        // when Assessment Criteria is requested by id
        Optional<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findById(savedAssessmentCriteriaEntity.getId());

        // then correct Assessment Criteria is returned
        assertTrue(result.isPresent());
        AssessmentCriteriaEntity assessmentCriteriaEntityResult = result.get();
        assertEquals(assessmentCriteriaEntity, assessmentCriteriaEntityResult);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenUnknownIdIsProvidedThenAssessmentCriteriaIsnNotReturned(){
        // given Assessment Criteria has been populated by Liquibase
        // when unknown id is provided
        Optional<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findById(1000000000L);
        // then no result is returned
        assertTrue(result.isEmpty());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateFromIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria record with given data is available
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentCriteriaEntity savedAssessmentCriteriaEntity = assessmentCriteriaRepository.save(assessmentCriteriaEntity);
        // when valid DATE_TO is given
        List<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findByDateFromAfter(TestModelDataBuilder.TEST_DATE_FROM.minusDays(1));
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateToIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria record with given data is available
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentCriteriaEntity savedAssessmentCriteriaEntity = assessmentCriteriaRepository.save(assessmentCriteriaEntity);
        // when valid DATE_TO is given
        List<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findByDateToBefore(TestModelDataBuilder.TEST_DATE_TO.plusHours(1));
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateToAndDateFromAreProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria record with given data is available
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentCriteriaEntity savedAssessmentCriteriaEntity = assessmentCriteriaRepository.save(assessmentCriteriaEntity);

        // when valid DATE_FROM and DATE_TO are given
        List<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findByDateFromAfterAndDateToBefore (TestModelDataBuilder.TEST_DATE_FROM.minusSeconds(1), TestModelDataBuilder.TEST_DATE_TO.plusSeconds(1));
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria record with given data is available
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentCriteriaEntity savedAssessmentCriteriaEntity = assessmentCriteriaRepository.save(assessmentCriteriaEntity);
        // when valid Date is given
        List<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1));
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
        AssessmentCriteriaEntity queryResult = result.stream().filter(ac -> ac.getId().equals(savedAssessmentCriteriaEntity.getId())).findAny().orElse(null);
        assertNotNull(queryResult);
        assertEquals(savedAssessmentCriteriaEntity, queryResult);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateIsProvidedAndDateToIsNullThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria record with given data is available
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        assessmentCriteriaEntity.setDateTo(null);
        AssessmentCriteriaEntity savedAssessmentCriteriaEntity = assessmentCriteriaRepository.save(assessmentCriteriaEntity);
        // when valid Date is given
        List<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1));
        // then at least one result is returned
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
        AssessmentCriteriaEntity queryResult = result.stream().filter(ac -> ac.getId().equals(savedAssessmentCriteriaEntity.getId())).findAny().orElse(null);
        assertNotNull(queryResult);
        assertEquals(savedAssessmentCriteriaEntity, queryResult);
        assertNull(queryResult.getDateTo());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenInvalidDateIsProvidedAThenAssessmentCriteriaShouldNotBeReturned(){
        // given Assessment Criteria record with given data is available
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentCriteriaEntity savedAssessmentCriteriaEntity = assessmentCriteriaRepository.save(assessmentCriteriaEntity);
        // when invalid Date is given
        List<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(TestModelDataBuilder.TEST_DATE_FROM.minusYears(100));
        // then no results are returned
        assertTrue(result.isEmpty());
    }

    @After
    public void tearDown() {
        if(assessmentCriteriaEntity != null) {
            assessmentCriteriaRepository.delete(assessmentCriteriaEntity);
            assessmentCriteriaEntity = null;
        }
    }
}