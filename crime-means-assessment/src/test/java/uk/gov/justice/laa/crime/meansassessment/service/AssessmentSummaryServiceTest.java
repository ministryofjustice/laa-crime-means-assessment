package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class AssessmentSummaryServiceTest {

    @Mock
    private CourtDataService courtDataService;

    @InjectMocks
    private AssessmentSummaryService assessmentSummaryService;

    private ApiCreateMeansAssessmentResponse meansAssessmentResponse;

    @Before
    public void setup() {
        meansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(true);
    }


    @Test
    public void givenMeansAssessmentResponse_WhenGetAssessmentSummaryIsInvoked_ThenAssessmentSummaryShouldBeReturnedWithFinAssSummary() {
        meansAssessmentResponse.setAssessmentType(AssessmentType.INIT);
        ApiCreateMeansAssessmentResponse response = assessmentSummaryService.getAssessmentsSummary(meansAssessmentResponse, null);
        assertThat(meansAssessmentResponse.getAssessmentSummary()).isNotNull();
    }


}
