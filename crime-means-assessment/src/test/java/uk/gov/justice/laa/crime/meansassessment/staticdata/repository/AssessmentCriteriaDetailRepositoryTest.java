package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentDetailEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AssessmentCriteriaDetailRepositoryTest {

    public static final int INVALID_ID = 1000;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AssessmentCriteriaDetailRepository assessmentCriteriaDetailRepository;

    private AssessmentCriteriaDetailEntity assessmentCriteriaDetailEntity;

    @Test
    public void givenAssessmentCriteriaDetailIsPopulated_WhenAllRecordsAreRequested_ThenAssessmentCriteriaDetailShouldBeReturned() {
        // given Assessment Criteria Details has been populated by Liquibase
        // when all records are requested
        Iterable<AssessmentCriteriaDetailEntity> results = assessmentCriteriaDetailRepository.findAll();
        // then at least one record is returned
        assertTrue(IterableUtils.size(results) > 0);
    }

    @Test
    public void givenAssessmentCriteriaDetailIsPopulated_WhenCorrectIdIsProvided_ThenAssessmentCriteriaDetailShouldBeReturned() {
        // given Assessment Criteria Detail record with given id is available
        AssessmentCriteriaDetailEntity assessmentCriteriaDetailEntity = TestModelDataBuilder.getAssessmentCriteriaDetailEntity();
        AssessmentCriteriaEntity assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentDetailEntity assessmentDetailEntity = TestModelDataBuilder.getAssessmentDetailEntity();
        assessmentCriteriaEntity = testEntityManager.persistAndFlush(assessmentCriteriaEntity);
        assessmentDetailEntity = testEntityManager.persistAndFlush(assessmentDetailEntity);
        assessmentCriteriaDetailEntity.setAssessmentCriteria(assessmentCriteriaEntity);
        assessmentCriteriaDetailEntity.setAssessmentDetail(assessmentDetailEntity);
        AssessmentCriteriaDetailEntity savedAssessmentCriteriaDetailEntity = assessmentCriteriaDetailRepository.save(assessmentCriteriaDetailEntity);

        // when Assessment Criteria Detail is requested by id
        Optional<AssessmentCriteriaDetailEntity> result = assessmentCriteriaDetailRepository.findById(savedAssessmentCriteriaDetailEntity.getId());

        // then correct Assessment Criteria Detail is returned
        assertTrue(result.isPresent());
        AssessmentCriteriaDetailEntity assessmentCriteriaDetailEntityResult = result.get();
        assertEquals(assessmentCriteriaDetailEntity, assessmentCriteriaDetailEntityResult);
    }

    @Test
    public void givenAssessmentCriteriaDetailIsPopulated_WhenUnknownIdIsProvided_ThenAssessmentCriteriaDetailIsnNotReturned() {
        // given Assessment Criteria Details has been populated by Liquibase
        // when unknown id is provided
        Optional<AssessmentCriteriaDetailEntity> result = assessmentCriteriaDetailRepository.findById(INVALID_ID);
        // then no result is returned
        assertTrue(result.isEmpty());
    }


    @After
    public void tearDown() {
        if (assessmentCriteriaDetailEntity != null) {
            assessmentCriteriaDetailRepository.delete(assessmentCriteriaDetailEntity);
            assessmentCriteriaDetailEntity = null;
        }
    }
}