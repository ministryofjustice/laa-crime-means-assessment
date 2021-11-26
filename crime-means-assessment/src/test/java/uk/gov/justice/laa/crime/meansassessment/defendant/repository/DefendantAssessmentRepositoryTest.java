package uk.gov.justice.laa.crime.meansassessment.defendant.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;

import static org.junit.Assert.assertEquals;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.DEFENDANT_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.DEFENDANT_ASSESSMENT_UPDATED_INFO;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DefendantAssessmentRepositoryTest {

    @Autowired
    private DefendantAssessmentRepository defendantAssessmentRepository;

    private DefendantAssessmentEntity defendantAssessmentEntity;

    @Before
    public void setUp() {
        defendantAssessmentEntity = DefendantAssessmentEntity.builder().id(DEFENDANT_ASSESSMENT_ID).updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO).build();
    }

    @After
    public void tearDown() {
        defendantAssessmentRepository.deleteAll();
        defendantAssessmentEntity = null;
    }

    @Test
    public void givenDefendantAssessmentEntityToAddShouldReturnAddedDefendantAssessment(){
        defendantAssessmentRepository.save(defendantAssessmentEntity);

        DefendantAssessmentEntity fetchedDefendantAssessmentEntity = defendantAssessmentRepository.findById(defendantAssessmentEntity.getId()).get();
        assertEquals(DEFENDANT_ASSESSMENT_ID, fetchedDefendantAssessmentEntity.getId());
    }
}