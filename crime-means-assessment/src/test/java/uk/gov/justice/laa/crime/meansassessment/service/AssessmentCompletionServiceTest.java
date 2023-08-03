package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class AssessmentCompletionServiceTest {

    private final String LAA_TRANSACTION_ID = "laa-transaction-id";
    private MeansAssessmentDTO assessment;
    @Spy
    @InjectMocks
    private AssessmentCompletionService assessmentCompletionService;

    @Mock
    private MaatCourtDataService maatCourtDataService;


    @BeforeEach
    void setup() {
        assessment = TestModelDataBuilder.getMeansAssessmentDTO();
    }

    @Test
    void givenNullExistingCompletionDate_whenIsFullUpdateRequiredIsInvoked_thenReturnTrue() {
        when(maatCourtDataService.getFinancialAssessment(anyInt(), anyString()))
                .thenReturn(FinancialAssessmentDTO.builder().build());

        boolean result = assessmentCompletionService.isFullUpdateRequired(assessment, LAA_TRANSACTION_ID);
        assertThat(result).isTrue();
    }

    @Test
    void givenExistingCompletionDate_whenIsFullUpdateRequiredIsInvoked_thenReturnFalse() {
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
    void givenNullFinancialAssessmentId_whenIsFullUpdateRequiredIsInvoked_thenReturnTrue() {
        assessment.getMeansAssessment().setFinancialAssessmentId(null);
        boolean result = assessmentCompletionService.isFullUpdateRequired(
                assessment, LAA_TRANSACTION_ID
        );
        assertThat(result).isTrue();
    }

    @Test
    void givenAssessment_whenUpdateApplicationCompletionDateIsInvoked_thenCompletionDateIsPersisted() {
        when(maatCourtDataService.updateCompletionDate(any(DateCompletionRequestDTO.class), anyString()))
                .thenReturn(TestModelDataBuilder.getRepOrderDTO());

        assessmentCompletionService.updateApplicationCompletionDate(assessment, LAA_TRANSACTION_ID);
        assertThat(LocalDate.now()).isEqualTo(assessment.getDateCompleted().toLocalDate());
    }

    @Test
    void givenPassedInitAssessmentResult_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.PASS);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isTrue();
    }

    @Test
    void givenFullInitAssessmentResult_whenIsInitAssessmentCompleteIsInvoked_thenReturnFalse() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FULL);
        assertThat(Boolean.FALSE).isEqualTo(assessmentCompletionService.isInitAssessmentComplete(assessment));
    }

    @Test
    void givenFailedSummaryOnly_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.SUMMARY_ONLY);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isTrue();
    }

    @Test
    void givenFailedCommital_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.COMMITAL);
        assertThat(Boolean.TRUE).isEqualTo(assessmentCompletionService.isInitAssessmentComplete(assessment));
    }

    @Test
    void givenFailedIndictable_whenIsInitAssessmentCompleteIsInvoked_thenReturnFalse() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.INDICTABLE);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isFalse();
    }

    @Test
    void givenFailedEitherWayAndCommittedForTrialsInMagsOutcome_whenIsInitAssessmentCompleteIsInvoked_thenReturnTrue() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.EITHER_WAY);
        assessment.getMeansAssessment().setMagCourtOutcome(MagCourtOutcome.COMMITTED_FOR_TRIAL);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isTrue();
    }

    @Test
    void givenFailedEitherWayAndAppealToCCMagsOutcome_whenIsInitAssessmentCompleteIsInvoked_thenReturnFalse() {
        assessment.setInitAssessmentResult(InitAssessmentResult.FAIL);
        assessment.getMeansAssessment().setCaseType(CaseType.EITHER_WAY);
        assessment.getMeansAssessment().setMagCourtOutcome(MagCourtOutcome.APPEAL_TO_CC);
        assertThat(assessmentCompletionService.isInitAssessmentComplete(assessment)).isFalse();
    }

    @Test
    void givenIncompleteAssessment_whenExecuteIsInvoked_thenCompletionDateIsNotUpdated() {
        assessment.getMeansAssessment().setAssessmentStatus(CurrentStatus.IN_PROGRESS);
        assessmentCompletionService.execute(assessment, LAA_TRANSACTION_ID);
        verify(maatCourtDataService, never())
                .updateCompletionDate(any(DateCompletionRequestDTO.class), anyString());
    }

    @Test
    void givenCompleteInitAssessment_whenExecuteIsInvoked_thenCompletionDateIsUpdated() {
        doReturn(true)
                .when(assessmentCompletionService).isInitAssessmentComplete(any(MeansAssessmentDTO.class));

        when(maatCourtDataService.updateCompletionDate(any(DateCompletionRequestDTO.class), anyString()))
                .thenReturn(TestModelDataBuilder.getRepOrderDTO());

        assessmentCompletionService.execute(assessment, LAA_TRANSACTION_ID);
        assertThat(LocalDate.now()).isEqualTo(assessment.getDateCompleted().toLocalDate());
    }

    @Test
    void givenCompleteFullAssessment_whenExecuteIsInvoked_thenCompletionDateIsUpdated() {
        assessment.getMeansAssessment().setAssessmentType(AssessmentType.FULL);
        doReturn(true)
                .when(assessmentCompletionService).isFullUpdateRequired(any(MeansAssessmentDTO.class), anyString());

        when(maatCourtDataService.updateCompletionDate(any(DateCompletionRequestDTO.class), anyString()))
                .thenReturn(TestModelDataBuilder.getRepOrderDTO());

        assessmentCompletionService.execute(assessment, LAA_TRANSACTION_ID);
        assertThat(LocalDate.now()).isEqualTo(assessment.getDateCompleted().toLocalDate());
    }

}
