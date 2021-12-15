package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import liquibase.repackaged.org.apache.commons.collections4.IterableUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentDetailEntity;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AssessmentDetailRepositoryTest {

    public static final String INVALID_ID = "INVALID_ID";
    @Autowired
    private AssessmentDetailRepository assessmentDetailRepository;

    private AssessmentDetailEntity assessmentDetailEntity;

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenAllRecordsAreRequestThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Details has been populated by Liquibase
        // when all records are requested
        Iterable<AssessmentDetailEntity> results = assessmentDetailRepository.findAll();
        // then at least one record is returned
        assertTrue(IterableUtils.size(results) > 0);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenCorrectIdIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Detail record with given id is available
        assessmentDetailEntity = TestModelDataBuilder.getAssessmentDetailEntity();
        AssessmentDetailEntity savedAssessmentDetailEntity = assessmentDetailRepository.save(assessmentDetailEntity);

        // when Assessment Detail is requested by id
        Optional<AssessmentDetailEntity> result = assessmentDetailRepository.findById(savedAssessmentDetailEntity.getDetailCode());

        // then correct Assessment Detail is returned
        assertTrue(result.isPresent());
        AssessmentDetailEntity assessmentDetailEntityResult = result.get();
        assertEquals(assessmentDetailEntity, assessmentDetailEntityResult);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenUnknownIdIsProvidedThenAssessmentCriteriaIsnNotReturned(){
        // given Assessment Details has been populated by Liquibase
        // when unknown id is provided
        Optional<AssessmentDetailEntity> result = assessmentDetailRepository.findById(INVALID_ID);
        // then no result is returned
        assertTrue(result.isEmpty());
    }


    @After
    public void tearDown() {
        if(assessmentDetailEntity != null) {
            assessmentDetailRepository.delete(assessmentDetailEntity);
            assessmentDetailEntity = null;
        }
    }
}