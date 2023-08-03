package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
 class AssessmentCriteriaDetailServiceTest {

    @InjectMocks
    private AssessmentCriteriaDetailService assessmentCriteriaDetailService;

    @Mock
    private AssessmentCriteriaDetailRepository assessmentCriteriaDetailRepository;

    @Test
     void testAssessmentCriteriaDetailService_whenGetAssessmentCriteriaDetailByIdInvoked_shouldSuccess() {
        assessmentCriteriaDetailService.getAssessmentCriteriaDetailById(41681827);
        verify(assessmentCriteriaDetailRepository, times(1)).findById(any());
    }

}