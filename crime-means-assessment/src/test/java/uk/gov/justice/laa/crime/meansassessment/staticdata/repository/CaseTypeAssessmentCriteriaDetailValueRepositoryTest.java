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
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.CaseTypeAssessmentCriteriaDetailValueEntity;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CaseTypeAssessmentCriteriaDetailValueRepositoryTest {

    public static final int INVALID_ID = 1000;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CaseTypeAssessmentCriteriaDetailValueRepository caseTypeAssessmentCriteriaDetailValueRepository;

    private CaseTypeAssessmentCriteriaDetailValueEntity caseTypeAssessmentCriteriaDetailValueEntity;

    @Test
    public void givenCaseTypeAssessmentCriteriaDetailValueIsPopulated_WhenAllRecordsAreRequest_ThenCaseTypeAssessmentCriteriaDetailValueShouldBeReturned() {
        // given CaseType Assessment Criteria Detail Value has been populated by Liquibase
        // when all records are requested
        Iterable<CaseTypeAssessmentCriteriaDetailValueEntity> results = caseTypeAssessmentCriteriaDetailValueRepository.findAll();
        // then at least one record is returned
        assertTrue(IterableUtils.size(results) > 0);
    }

    @Test
    public void givenCaseTypeAssessmentCriteriaDetailValueIsPopulated_WhenCorrectIdIsProvided_ThenCaseTypeAssessmentCriteriaDetailValueShouldBeReturned() {
        // given CaseType Assessment Criteria Detail Value record with given id is available
        AssessmentCriteriaDetailEntity assessmentCriteriaDetailEntity = TestModelDataBuilder.getAssessmentCriteriaDetailEntity();
        AssessmentCriteriaEntity assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        AssessmentDetailEntity assessmentDetailEntity = TestModelDataBuilder.getAssessmentDetailEntity();
        assessmentCriteriaEntity = testEntityManager.persistAndFlush(assessmentCriteriaEntity);
        assessmentDetailEntity = testEntityManager.persistAndFlush(assessmentDetailEntity);
        assessmentCriteriaDetailEntity.setAssessmentCriteria(assessmentCriteriaEntity);
        assessmentCriteriaDetailEntity.setAssessmentDetail(assessmentDetailEntity);
        assessmentCriteriaDetailEntity = testEntityManager.persistAndFlush(assessmentCriteriaDetailEntity);

        caseTypeAssessmentCriteriaDetailValueEntity = TestModelDataBuilder.getCaseTypeAssessmentCriteriaDetailValueEntity();
        caseTypeAssessmentCriteriaDetailValueEntity.setAssessmentCriteriaDetail(assessmentCriteriaDetailEntity);

        CaseTypeAssessmentCriteriaDetailValueEntity savedAssessmentCriteriaDetailFrequencyEntity = caseTypeAssessmentCriteriaDetailValueRepository.save(caseTypeAssessmentCriteriaDetailValueEntity);

        // when CaseType Assessment Criteria Detail Value is requested by id
        Optional<CaseTypeAssessmentCriteriaDetailValueEntity> result = caseTypeAssessmentCriteriaDetailValueRepository.findById(savedAssessmentCriteriaDetailFrequencyEntity.getId());

        // then correct CaseType Assessment Criteria Detail Value is returned
        assertTrue(result.isPresent());
        CaseTypeAssessmentCriteriaDetailValueEntity caseTypeAssessmentCriteriaDetailValueEntity = result.get();
        assertEquals(caseTypeAssessmentCriteriaDetailValueEntity, savedAssessmentCriteriaDetailFrequencyEntity);
    }

    @Test
    public void givenCaseTypeAssessmentCriteriaDetailValueIsPopulated_WhenUnknownIdIsProvided_ThenCaseTypeAssessmentCriteriaDetailValueIsnNotReturned() {
        // given CaseType Assessment Criteria Detail Value has been populated by Liquibase
        // when unknown id is provided
        Optional<CaseTypeAssessmentCriteriaDetailValueEntity> result = caseTypeAssessmentCriteriaDetailValueRepository.findById(INVALID_ID);
        // then no result is returned
        assertTrue(result.isEmpty());
    }


    @After
    public void tearDown() {
        if (caseTypeAssessmentCriteriaDetailValueEntity != null) {
            caseTypeAssessmentCriteriaDetailValueRepository.delete(caseTypeAssessmentCriteriaDetailValueEntity);
            caseTypeAssessmentCriteriaDetailValueEntity = null;
        }
    }
}