package uk.gov.justice.laa.crime.meansassessment.defendant.service;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.defendant.repository.DefendantAssessmentRepository;

import static uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder.DEFENDANT_ASSESSMENT_ID;

@RunWith(MockitoJUnitRunner.class)
public class DefendantAssessmentServiceTest {

    @InjectMocks
    private DefendantAssessmentService defendantAssessmentService;

    @Mock
    private DefendantAssessmentRepository defendantAssessmentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenDefendantAssessmentID_ThenGetDefendantAssessmentDTO(){
        //given
//        DefendantAssessmentEntity defendantAssessmentDTO = TestModelDataBuilder.getDefendantAssessmentDTO();
        //when
        DefendantAssessmentEntity defendantAssessmentDTO = defendantAssessmentService.findById(DEFENDANT_ASSESSMENT_ID);

//        verify(defendantAssessmentRepository).findById(DEFENDANT_ASSESSMENT_ID);

//        assertThat(defendantAssessmentDTOReturned.getId().toString()).isEqualTo(DEFENDANT_ASSESSMENT_ID);
//        assertThat(defendantAssessmentDTOReturned.getUpdatedInfo()).isEqualTo(DEFENDANT_ASSESSMENT_UPDATED_INFO);

    }
}