package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import static org.junit.Assert.*;

@DataJpaTest
@RunWith(SpringRunner.class)
public class AssessmentCriteriaRepositoryTest {

    private AssessmentCriteriaEntity assessmentCriteriaEntity;

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    @Before
    public void setup() {
        Integer latestCriteriaId = 34;
        assessmentCriteriaEntity = assessmentCriteriaRepository.findById(latestCriteriaId).orElse(null);
        if (assessmentCriteriaEntity == null) {
            throw new RuntimeException("Assessment Criteria not found.");
        }
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