package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AssessmentCriteriaRepositoryTest {

    private AssessmentCriteriaEntity assessmentCriteriaEntity;

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    @Before
    public void setup() {
        Integer latestCriteriaId = 34;
        assessmentCriteriaEntity = assessmentCriteriaRepository.findById(latestCriteriaId).orElse(null);
        if (assessmentCriteriaEntity ==  null) {
            throw new RuntimeException("Assessment Criteria not found.");
        }
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenAllRecordsAreRequest_ThenAssessmentCriteriaShouldBeReturned() {
        // given Assessment Criteria has been populated by Liquibase
        // when all records are requested
        Iterable<AssessmentCriteriaEntity> results = assessmentCriteriaRepository.findAll();
        // then at least one record is returned
        assertTrue(IterableUtils.size(results) > 0);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenCorrectIdIsProvided_ThenAssessmentCriteriaShouldBeReturned() {
        // when Assessment Criteria is requested by id
        Optional<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findById(assessmentCriteriaEntity.getId());

        // then correct Assessment Criteria is returned
        assertTrue(result.isPresent());
        AssessmentCriteriaEntity assessmentCriteriaEntityResult = result.get();
        assertEquals(assessmentCriteriaEntity, assessmentCriteriaEntityResult);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenUnknownIdIsProvided_ThenAssessmentCriteriaIsnNotReturned() {
        // given Assessment Criteria has been populated by Liquibase
        // when unknown id is provided
        Optional<AssessmentCriteriaEntity> result = assessmentCriteriaRepository.findById(1000);
        // then no result is returned
        assertTrue(result.isEmpty());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenValidDateIsProvided_ThenAssessmentCriteriaShouldBeReturned() {
        // when valid Date is given
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1));
        // then at least one result is returned
        assertEquals(assessmentCriteriaEntity, result);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenValidDateIsProvidedAndDateToIsNull_ThenAssessmentCriteriaShouldBeReturned() {
        // when valid Date is given
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(TestModelDataBuilder.TEST_DATE_FROM.plusHours(1));
        // then at least one result is returned
        assertEquals(assessmentCriteriaEntity, result);
        assertNull(result.getDateTo());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenInvalidDateIsProvided_ThenAssessmentCriteriaShouldNotBeReturned() {
        // when invalid Date is given
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(TestModelDataBuilder.TEST_DATE_FROM.minusYears(100));
        // then no results are returned
        assertNull(result);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulated_WhenOldValidDateIsProvided_ThenAssessmentCriteriaShouldBeReturned() {
        AssessmentCriteriaEntity result = assessmentCriteriaRepository.findAssessmentCriteriaForDate(assessmentCriteriaEntity.getDateFrom().minusDays(1));
        assertNotEquals(assessmentCriteriaEntity, result);
    }

    @After
    public void tearDown() {
        if (assessmentCriteriaEntity != null) {
            assessmentCriteriaRepository.delete(assessmentCriteriaEntity);
            assessmentCriteriaEntity = null;
        }
    }
}