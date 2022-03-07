package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FullAssessmentAvailabilityServiceTest {

    private FullAssessmentAvailabilityService fullAssessmentAvailabilityService;

    private ApiCreateMeansAssessmentRequest meansAssessmentRequest;

    private ApiCreateMeansAssessmentResponse meansAssessmentResponse;

    @Before
    public void setup() {
        fullAssessmentAvailabilityService = new FullAssessmentAvailabilityService();
        meansAssessmentRequest = TestModelDataBuilder.getCreateMeansAssessmentRequest(true);
        meansAssessmentResponse = TestModelDataBuilder.getCreateMeansAssessmentResponse(true);
        meansAssessmentRequest.setFullAssessmentDate(null);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenFullAssessmentDateIsNotNull_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentResponse.setResult("");
        meansAssessmentRequest.setFullAssessmentDate(LocalDateTime.of(2021, 12, 20, 10, 0));

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsFull_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentResponse.setResult("FULL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsIndictable_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setCaseType(CaseType.INDICTABLE);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsCCAlready_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setCaseType(CaseType.CC_ALREADY);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsAppealCC_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setCaseType(CaseType.APPEAL_CC);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeNull_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setCaseType(CaseType.EITHER_WAY);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeCommittedForTrial_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setCaseType(CaseType.EITHER_WAY);
        meansAssessmentRequest.setMagCourtOutcome(MagCourtOutcome.COMMITTED_FOR_TRIAL);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeAppealCC_ThenFullAssessmentAvailableIsFalse() {
        meansAssessmentRequest.setCaseType(CaseType.EITHER_WAY);
        meansAssessmentRequest.setMagCourtOutcome(MagCourtOutcome.APPEAL_TO_CC);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsHardshipAndNewWorkReasonNull_ThenFullAssessmentAvailableIsFalse() {
        meansAssessmentResponse.setResult("HARDSHIP APPLICATION");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsHardshipAndNewWorkReasonIsHR_ThenFullAssessmentAvailableIsTrue() {
        meansAssessmentResponse.setResult("HARDSHIP APPLICATION");
        meansAssessmentRequest.setNewWorkReason(NewWorkReason.HR);

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsHardshipAndNewWorkReasonIsPBI_ThenFullAssessmentAvailableIsFalse() {
        meansAssessmentResponse.setResult("HARDSHIP APPLICATION");
        meansAssessmentRequest.setNewWorkReason(NewWorkReason.PBI);

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsNone_ThenFullAssessmentAvailableIsFalse() {
        meansAssessmentResponse.setResult("NONE");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void givenMeansAssessmentRequestAndResponse_WhenResultIsEmpty_ThenFullAssessmentAvailableIsFalse() {
        meansAssessmentResponse.setResult("");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

}
