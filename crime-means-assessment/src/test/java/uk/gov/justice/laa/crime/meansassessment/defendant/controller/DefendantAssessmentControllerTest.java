package uk.gov.justice.laa.crime.meansassessment.defendant.controller;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.repository.DefendantAssessmentRepository;
import uk.gov.justice.laa.crime.meansassessment.defendant.service.DefendantAssessmentService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.mockito.Mockito.verify;
import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.*;

@RunWith(MockitoJUnitRunner.class)
public class DefendantAssessmentControllerTest {

    private TestModelDataBuilder testModelDataBuilder;

    @InjectMocks
    private DefendantAssessmentController defendantAssessmentController;

    @Mock
    private DefendantAssessmentService defendantAssessmentService;

    @Mock
    private DefendantAssessmentRepository defendantAssessmentRepository;


    //Make call to get defendant assessment - pass defendant assessment id
    //assert defendant object is returned
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenValidDefendantIDIsPassed_whenGetControllerIsInvoked_thenRetrieveDefendantAssessmmentObject(){

        DefendantAssessmentEntity defendantAssessmentDTO = TestModelDataBuilder.getDefendantAssessmentDTO();

        ResponseEntity<DefendantAssessmentEntity> response = defendantAssessmentController.getDefendantAssessment(DEFENDANT_ASSESSMENT_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(defendantAssessmentService).findById(DEFENDANT_ASSESSMENT_ID);

//        verify(defendantAssessmentRepository).findById(DEFENDANT_ASSESSMENT_ID);


//        assertThat(response.getBody().getId().toString()).isEqualTo(DEFENDANT_ASSESSMENT_ID);
//        assertThat(response.getBody().getUpdatedInfo()).isEqualTo(DEFENDANT_ASSESSMENT_UPDATED_INFO);
    }
}