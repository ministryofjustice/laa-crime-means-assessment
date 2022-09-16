package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCriteriaDetailServiceTest {

    @InjectMocks
    private AssessmentCriteriaDetailService assessmentCriteriaDetailService;

    @Mock
    private AssessmentCriteriaDetailRepository assessmentCriteriaDetailRepository;

    @Test
    public void testAssessmentCriteriaDetailService_whenGetAssessmentCriteriaDetailByIdInvoked_shouldSuccess() {
        assessmentCriteriaDetailService.getAssessmentCriteriaDetailById(41681827);
        verify(assessmentCriteriaDetailRepository, times(1)).findById(any());
    }

}