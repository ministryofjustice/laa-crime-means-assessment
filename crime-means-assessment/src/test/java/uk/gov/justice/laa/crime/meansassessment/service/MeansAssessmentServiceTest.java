package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.validation.InitialAssessmentValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentServiceTest {

    @InjectMocks
    private MeansAssessmentService meansAssessmentService;

    @Mock
    private InitialMeansAssessmentService initialMeansAssessmentService;
    @Mock
    private FullMeansAssessmentService fullMeansAssessmentService;
    @Mock
    private InitialAssessmentValidator initialAssessmentValidator;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private AssessmentSummaryService assessmentSummaryService;

    @Test
    public void testWhenAssessmentTypeIsInit_thenVerifyInitAssessmentIsCalled() throws ValidationException {

        ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        apiCreateMeansAssessmentRequest.setAssessmentType(AssessmentType.INIT);
        apiCreateMeansAssessmentRequest.setCrownCourtOverview(getApiCrownCourtOverview());
        ApiReviewType reviewType = new ApiReviewType();
        reviewType.setCode("SDS");
        apiCreateMeansAssessmentRequest.setReviewType(reviewType);

        ApiCreateMeansAssessmentResponse apiCreateMeansAssessmentResponse = new ApiCreateMeansAssessmentResponse();

        when(initialMeansAssessmentService.createInitialAssessment(apiCreateMeansAssessmentRequest)).thenReturn(apiCreateMeansAssessmentResponse);

        meansAssessmentService.createAssessment(apiCreateMeansAssessmentRequest);

        verify(initialAssessmentValidator).validate(any());
        verify(initialMeansAssessmentService).createInitialAssessment(any());

    }

    private ApiCrownCourtOverview getApiCrownCourtOverview(){

        ApiCrownCourtSummary apiCrownCourtSummary = new ApiCrownCourtSummary();
        apiCrownCourtSummary.setRepOrderDecision("APPROVED");

        ApiCrownCourtOverview apiCrownCourtOverview = new ApiCrownCourtOverview();
        apiCrownCourtOverview.setCrownCourtSummary(apiCrownCourtSummary);

        return apiCrownCourtOverview;

    }
}