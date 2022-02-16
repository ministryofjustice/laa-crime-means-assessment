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
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailFrequencyEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentDetailEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AssessmentCriteriaDetailFrequencyRepositoryTest {

    public static final int INVALID_ID = 1000;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AssessmentCriteriaDetailFrequencyRepository assessmentCriteriaDetailFrequencyRepository;

    private AssessmentCriteriaDetailFrequencyEntity assessmentCriteriaDetailFrequency;

    @Test
    public void givenAssessmentCriteriaDetailFrequencyIsPopulated_WhenAllRecordsAreRequest_ThenAssessmentCriteriaDetailFrequencyShouldBeReturned(){
        // given Assessment Criteria Detail Frequency has been populated by Liquibase
        // when all records are requested
        Iterable<AssessmentCriteriaDetailFrequencyEntity> results = assessmentCriteriaDetailFrequencyRepository.findAll();
        // then at least one record is returned
        assertTrue(IterableUtils.size(results) > 0);
    }

    @Test
    public void givenAssessmentCriteriaDetailFrequencyIsPopulated_WhenCorrectIdIsProvided_ThenAssessmentCriteriaDetailFrequencyShouldBeReturned(){
        // given Assessment Criteria Detail Frequency record with given id is available
        AssessmentCriteriaDetailEntity assessmentCriteriaDetailEntity = TestModelDataBuilder.getAssessmentCriteriaDetailEntity();
        AssessmentCriteriaEntity assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentDetailEntity assessmentDetailEntity = TestModelDataBuilder.getAssessmentDetailEntity();
        assessmentCriteriaEntity = testEntityManager.persistAndFlush(assessmentCriteriaEntity);
        assessmentDetailEntity = testEntityManager.persistAndFlush(assessmentDetailEntity);
        assessmentCriteriaDetailEntity.setAssessmentCriteria(assessmentCriteriaEntity);
        assessmentCriteriaDetailEntity.setAssessmentDetail(assessmentDetailEntity);
        assessmentCriteriaDetailEntity = testEntityManager.persistAndFlush(assessmentCriteriaDetailEntity);

        AssessmentCriteriaDetailFrequencyEntity assessmentCriteriaDetailFrequencyEntity = TestModelDataBuilder.getAssessmentCriteriaDetailFrequencyEntity();
        assessmentCriteriaDetailFrequencyEntity.setAssessmentCriteriaDetail(assessmentCriteriaDetailEntity);

        AssessmentCriteriaDetailFrequencyEntity savedAssessmentCriteriaDetailFrequencyEntity = assessmentCriteriaDetailFrequencyRepository.save(assessmentCriteriaDetailFrequencyEntity);

        // when Assessment Criteria Detail Frequency is requested by id
        Optional<AssessmentCriteriaDetailFrequencyEntity> result = assessmentCriteriaDetailFrequencyRepository.findById(savedAssessmentCriteriaDetailFrequencyEntity.getId());

        // then correct Assessment Criteria Detail Frequency is returned
        assertTrue(result.isPresent());
        AssessmentCriteriaDetailFrequencyEntity assessmentCriteriaDetailFrequencyEntityResult = result.get();
        assertEquals(assessmentCriteriaDetailFrequencyEntityResult, savedAssessmentCriteriaDetailFrequencyEntity);
    }

    @Test
    public void givenAssessmentCriteriaDetailFrequencyIsPopulated_WhenUnknownIdIsProvided_ThenAssessmentCriteriaDetailFrequencyIsnNotReturned(){
        // given Assessment Criteria Detail Frequency has been populated by Liquibase
        // when unknown id is provided
        Optional<AssessmentCriteriaDetailFrequencyEntity> result = assessmentCriteriaDetailFrequencyRepository.findById(INVALID_ID);
        // then no result is returned
        assertTrue(result.isEmpty());
    }


    @After
    public void tearDown() {
        if(assessmentCriteriaDetailFrequency != null) {
            assessmentCriteriaDetailFrequencyRepository.delete(assessmentCriteriaDetailFrequency);
            assessmentCriteriaDetailFrequency = null;
        }
    }
}