package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCriteriaDetailServiceTest {

    @InjectMocks
    private AssessmentCriteriaDetailService assessmentCriteriaDetailService;

    @Mock
    private  AssessmentCriteriaDetailRepository assessmentCriteriaDetailRepository;

    @Test
    public void testAssessmentCriteriaDetailService_whenGetAssessmentCriteriaDetailByIdInvoked_shouldSuccess() {
        Optional<AssessmentCriteriaDetailEntity> assessmentCriteriaDetailEntity= assessmentCriteriaDetailService.getAssessmentCriteriaDetailById(41681827);
        verify(assessmentCriteriaDetailRepository, times(1)).findById(any());
    }

}