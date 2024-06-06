package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class FullAssessmentAvailabilityServiceTest {

    private FullAssessmentAvailabilityService fullAssessmentAvailabilityService;

    private MeansAssessmentRequestDTO meansAssessmentRequest;

    @BeforeEach
    void setup() {
        fullAssessmentAvailabilityService = new FullAssessmentAvailabilityService();
        meansAssessmentRequest = TestModelDataBuilder.getMeansAssessmentRequestDTO(true);
        meansAssessmentRequest.setFullAssessmentDate(null);
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFull_ThenFullAssessmentAvailableIsTrue() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                meansAssessmentRequest.getCaseType(),
                meansAssessmentRequest.getMagCourtOutcome(),
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FULL)).isTrue();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsIndictable_ThenFullAssessmentAvailableIsTrue() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.INDICTABLE,
                meansAssessmentRequest.getMagCourtOutcome(),
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isTrue();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsCCAlready_ThenFullAssessmentAvailableIsTrue() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.CC_ALREADY,
                meansAssessmentRequest.getMagCourtOutcome(),
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isTrue();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsAppealCC_ThenFullAssessmentAvailableIsTrue() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.APPEAL_CC,
                meansAssessmentRequest.getMagCourtOutcome(),
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isTrue();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsCommittal_ThenFullAssessmentAvailableIsFalse() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.COMMITAL,
                meansAssessmentRequest.getMagCourtOutcome(),
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isFalse();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsSummaryOnly_ThenFullAssessmentAvailableIsFalse() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.SUMMARY_ONLY,
                meansAssessmentRequest.getMagCourtOutcome(),
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isFalse();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeNull_ThenFullAssessmentAvailableIsTrue() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.EITHER_WAY,
                MagCourtOutcome.COMMITTED,
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isFalse();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeCommittedForTrial_ThenFullAssessmentAvailableIsTrue() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.EITHER_WAY,
                MagCourtOutcome.COMMITTED_FOR_TRIAL,
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isTrue();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsFailAndCaseTypeIsEitherWayWithMagOutcomeAppealCC_ThenFullAssessmentAvailableIsFalse() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                CaseType.EITHER_WAY,
                MagCourtOutcome.APPEAL_TO_CC,
                meansAssessmentRequest.getNewWorkReason(),
                InitAssessmentResult.FAIL)).isFalse();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsHardshipAndNewWorkReasonNull_ThenFullAssessmentAvailableIsFalse() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                meansAssessmentRequest.getCaseType(),
                meansAssessmentRequest.getMagCourtOutcome(),
                null,
                InitAssessmentResult.HARDSHIP)).isFalse();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsHardshipAndNewWorkReasonIsHR_ThenFullAssessmentAvailableIsTrue() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                meansAssessmentRequest.getCaseType(),
                meansAssessmentRequest.getMagCourtOutcome(),
                NewWorkReason.HR,
                InitAssessmentResult.HARDSHIP)).isTrue();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsHardshipAndNewWorkReasonIsPBI_ThenFullAssessmentAvailableIsFalse() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                meansAssessmentRequest.getCaseType(),
                meansAssessmentRequest.getMagCourtOutcome(),
                NewWorkReason.PBI,
                InitAssessmentResult.HARDSHIP)).isFalse();
    }

    @Test
    void givenMeansAssessmentRequestAndResponse_WhenResultIsNull_ThenFullAssessmentAvailableIsFalse() {
        assertThat(fullAssessmentAvailabilityService.isFullAssessmentAvailable(
                meansAssessmentRequest.getCaseType(),
                meansAssessmentRequest.getMagCourtOutcome(),
                meansAssessmentRequest.getNewWorkReason(),
                null)).isFalse();
    }
}
