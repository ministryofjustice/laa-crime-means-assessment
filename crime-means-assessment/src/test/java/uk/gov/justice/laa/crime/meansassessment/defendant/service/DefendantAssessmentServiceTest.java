package uk.gov.justice.laa.crime.meansassessment.defendant.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.repository.DefendantAssessmentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.DEFENDANT_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.DEFENDANT_ASSESSMENT_UPDATED_INFO;

@RunWith(MockitoJUnitRunner.class)
public class DefendantAssessmentServiceTest {

    @InjectMocks
    private DefendantAssessmentService defendantAssessmentService;

    @Spy
    private DefendantAssessmentRepository defendantAssessmentRepository;

    private DefendantAssessmentEntity defendantAssessmentEntity;

    @Before
    public void setUp() {
        //given
        var defendantAssessment = TestModelDataBuilder.getDefendantAssessmentDTO();
        //when
        when(defendantAssessmentRepository.findById(DEFENDANT_ASSESSMENT_ID))
                .thenReturn(Optional.of(defendantAssessment));
    }

    @Test
    public void givenDefendantAssessmentID_ThenGetDefendantAssessment(){
        var defendantAssessmentReturned = defendantAssessmentService.findById(DEFENDANT_ASSESSMENT_ID);
        assertEquals(DEFENDANT_ASSESSMENT_ID,defendantAssessmentReturned.getId());
    }

    @Test
    public void givenDefendantAssessmentObject_ThenGetSaveDefendantAssessmentObject(){
        //given
        var testDefendantAssessmentEntity = DefendantAssessmentEntity.builder().id(DEFENDANT_ASSESSMENT_ID).updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO).build();
        //and
        defendantAssessmentEntity = TestModelDataBuilder.getDefendantAssessmentDTO();
        //when
        when(defendantAssessmentRepository.save(testDefendantAssessmentEntity))
                .thenReturn(testDefendantAssessmentEntity);

        var defendantAssessmentReturned = defendantAssessmentService.save(defendantAssessmentEntity);
        assertEquals(DEFENDANT_ASSESSMENT_ID,defendantAssessmentReturned.getId());
    }
}