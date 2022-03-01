package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.client.AuthorisationMeansAssessmentClient;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.exception.MeansAssessmentValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.AuthorizationResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiNewWorkReason;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MeansAssessmentServiceTest {

    @InjectMocks
    private MeansAssessmentService meansAssessmentService;

    @Mock
    private AuthorisationMeansAssessmentClient workReasonsClient;


    @Test (expected = MeansAssessmentValidationException.class)
    public void testWhenAuthorisationResponseTypeIsNull() throws MeansAssessmentValidationException, AssessmentCriteriaNotFoundException {
        meansAssessmentService.checkInitialAssessment(new ApiCreateMeansAssessmentRequest());
    }

    private ApiCreateMeansAssessmentRequest getApiCreateMeansAssessmentRequest() {
        ApiCreateMeansAssessmentRequest mas = new ApiCreateMeansAssessmentRequest();
        mas.setTransactionDateTime(LocalDateTime.of(2021, 12, 16, 10, 0));
        mas.setAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0));
        ApiNewWorkReason apiNewWorkReason = new ApiNewWorkReason();
        apiNewWorkReason.setCode("1234");
        mas.setNewWorkReason(apiNewWorkReason);
        mas.setHasPartner(true);
        mas.setPartnerContraryInterest(false);

        return mas;
    }


}