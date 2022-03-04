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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
    }

    @Test
    public void whenAssessmentDateIsNotNull_thenFullAssessmentAvailableIsTrue() {
        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void whenResultIsFull_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentResponse.setResult("FULL");
        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void whenResultIsFailAndCaseTypeIsIndictable_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentRequest.setCaseType(CaseType.INDICTABLE);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void whenResultIsFailAndCaseTypeIsCCAlready_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentRequest.setCaseType(CaseType.CC_ALREADY);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void whenResultIsFailAndCaseTypeIsAppealCC_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentRequest.setCaseType(CaseType.APPEAL_CC);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void whenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeNull_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentRequest.setCaseType(CaseType.EITHER_WAY);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void whenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeCommittedForTrial_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentRequest.setCaseType(CaseType.EITHER_WAY);
        meansAssessmentRequest.setMagCourtOutcome(MagCourtOutcome.COMMITTED_FOR_TRIAL);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void whenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeAppealCC_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentRequest.setCaseType(CaseType.EITHER_WAY);
        meansAssessmentRequest.setMagCourtOutcome(MagCourtOutcome.APPEAL_TO_CC);
        meansAssessmentResponse.setResult("FAIL");

        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void whenResultIsHardshipAndNewWorkReasonNull_thenFullAssessmentAvailableIsFalse() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentResponse.setResult("HARDSHIP");
        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void whenResultIsHardshipAndNewWorkReasonIsHR_thenFullAssessmentAvailableIsTrue() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentResponse.setResult("HARDSHIP");
        meansAssessmentRequest.setNewWorkReason(NewWorkReason.HR);
        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(true);
    }

    @Test
    public void whenResultIsHardshipAndNewWorkReasonIsPBI_thenFullAssessmentAvailableIsFalse() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentResponse.setResult("HARDSHIP");
        meansAssessmentRequest.setNewWorkReason(NewWorkReason.PBI);
        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void whenResultIsNone_thenFullAssessmentAvailableIsFalse() {
        meansAssessmentRequest.setAssessmentDate(null);
        meansAssessmentResponse.setResult("NONE");
        fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, meansAssessmentResponse);
        assertThat(meansAssessmentResponse.getFullAssessmentAvailable()).isEqualTo(false);
    }

    @Test
    public void whenMeansRequestIsNull_thenThrowNPE() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> fullAssessmentAvailabilityService.processFullAssessmentAvailable(null, meansAssessmentResponse))
                .withMessageContaining("meansAssessmentRequest must not be null");
    }

    @Test
    public void whenMeansResponseIsNull_thenThrowNPE() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> fullAssessmentAvailabilityService.processFullAssessmentAvailable(meansAssessmentRequest, null))
                .withMessageContaining("meansAssessmentResponse must not be null");
    }

}
