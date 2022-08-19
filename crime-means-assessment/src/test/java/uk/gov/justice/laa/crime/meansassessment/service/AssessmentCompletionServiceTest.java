package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.DateCompletionRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCompletionServiceTest {

    private MeansAssessmentDTO assessment;
    private final String LAA_TRANSACTION_ID = "laa-transaction-id";

    @Spy
    @InjectMocks
    private AssessmentCompletionService assessmentCompletionService;

    @Mock
    private MaatCourtDataService maatCourtDataService;


    @Before
    public void setup() {
        assessment = TestModelDataBuilder.getMeansAssessmentDTO();
    }

    @Test
    public void givenNullExistingCompletionDate_whenIsFullUpdateRequiredIsInvoked_thenReturnTrue() {
        when(maatCourtDataService.getFinancialAssessment(anyInt(), anyString()))
                .thenReturn(FinancialAssessmentDTO.builder().build());

        boolean result = assessmentCompletionService.isFullUpdateRequired(assessment, LAA_TRANSACTION_ID);
        assertThat(result).isTrue();
    }

    @Test
    public void givenExistingCompletionDate_whenIsFullUpdateRequiredIsInvoked_thenReturnFalse() {
        when(maatCourtDataService.getFinancialAssessment(anyInt(), anyString()))
                .thenReturn(
                        FinancialAssessmentDTO
                                .builder()
                                .dateCompleted(LocalDateTime.now())
                                .build()
                );

        boolean result = assessmentCompletionService.isFullUpdateRequired(assessment, LAA_TRANSACTION_ID);
        assertThat(result).isFalse();
    }

    @Test
    public void givenNullFinancialAssessmentId_whenIsFullUpdateRequiredIsInvoked_thenReturnTrue() {
        assessment.getMeansAssessment().setFinancialAssessmentId(null);
        boolean result = assessmentCompletionService.isFullUpdateRequired(
                assessment, LAA_TRANSACTION_ID
        );
        assertThat(result).isTrue();
    }

    @Test
    public void givenAssessment_whenUpdateApplicationCompletionDateIsInvoked_thenCompletionDateIsPersisted() {
        assessmentCompletionService.updateApplicationCompletionDate(assessment, LAA_TRANSACTION_ID);

        assertThat(LocalDate.now()).isEqualTo(assessment.getDateCompleted().toLocalDate());
        verify(maatCourtDataService).updateCompletionDate(any(DateCompletionRequestDTO.class), anyString());
    }

    @Test
    public void givenPassedInitAssessmentResult_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.PASS);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isTrue();
    }

    @Test
    public void givenFullInitAssessmentResult_whenIsInitAssessmentCompleteIsInvoked_thenReturnFalse() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FULL);
        assertThat(Boolean.FALSE).isEqualTo(assessmentCompletionService.isInitAssessmentComplete(assessment));
    }

    @Test
    public void givenFailedSummaryOnly_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.SUMMARY_ONLY);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isTrue();
    }

    @Test
    public void givenFailedCommital_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.COMMITAL);
        assertThat(Boolean.TRUE).isEqualTo(assessmentCompletionService.isInitAssessmentComplete(assessment));
    }

    @Test
    public void givenFailedIndictable_whenIsInitAssessmentCompleteIsInvoked_thenReturnFalse() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.INDICTABLE);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isFalse();
    }

    @Test
    public void givenFailedEitherWayAndCommittedForTrialsInMagsOutcome_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.EITHER_WAY);
        assessment.getMeansAssessment().setMagCourtOutcome(MagCourtOutcome.COMMITTED_FOR_TRIAL);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isTrue();
    }

    @Test
    public void givenFailedEitherWayAndAppealToCCMagsOutcome_whenIsInitAssessmentCompleteIsInvoked_thenReturnFalse() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.EITHER_WAY);
        assessment.getMeansAssessment().setMagCourtOutcome(MagCourtOutcome.APPEAL_TO_CC);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isFalse();
    }

    @Test
    public void givenIncompleteAssessment_whenExecuteIsInvoked_thenCompletionDateIsNotUpdated() {
        assessment.getMeansAssessment().setAssessmentStatus(CurrentStatus.IN_PROGRESS);
        assessmentCompletionService.execute(assessment, LAA_TRANSACTION_ID);
        verify(maatCourtDataService, never())
                .updateCompletionDate(any(DateCompletionRequestDTO.class), anyString());
    }

    @Test
    public void givenCompleteInitAssessment_whenExecuteIsInvoked_thenCompletionDateIsUpdated() {
        doReturn(true)
                .when(assessmentCompletionService).isInitAssessmentComplete(any(MeansAssessmentDTO.class));
        assessmentCompletionService.execute(assessment, LAA_TRANSACTION_ID);
        assertThat(LocalDate.now()).isEqualTo(assessment.getDateCompleted().toLocalDate());
        verify(maatCourtDataService)
                .updateCompletionDate(any(DateCompletionRequestDTO.class), anyString());
    }

    @Test
    public void givenCompleteFullAssessment_whenExecuteIsInvoked_thenCompletionDateIsUpdated() {
        assessment.getMeansAssessment().setAssessmentType(AssessmentType.FULL);
        doReturn(true)
                .when(assessmentCompletionService).isFullUpdateRequired(any(MeansAssessmentDTO.class), anyString());
        assessmentCompletionService.execute(assessment, LAA_TRANSACTION_ID);
        assertThat(LocalDate.now()).isEqualTo(assessment.getDateCompleted().toLocalDate());
        verify(maatCourtDataService)
                .updateCompletionDate(any(DateCompletionRequestDTO.class), anyString());
    }

}
