package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.DateCompletionRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatAssessmentCompletionService {

    private final MaatCourtDataService maatCourtDataService;

    public void execute(MeansAssessmentDTO assessment, AssessmentType type, String laaTransactionId) {
        boolean isUpdateRequired = false;
        CurrentStatus assessmentStatus = assessment.getMeansAssessment().getAssessmentStatus();
        if (CurrentStatus.COMPLETE.equals(assessmentStatus)) {
            if (AssessmentType.INIT.equals(type)) {
                isUpdateRequired = isInitAssessmentComplete(assessment);
            } else {
                isUpdateRequired = isFullUpdateRequired(assessment, laaTransactionId);
            }
        }

        if (isUpdateRequired) {
            updateApplicationCompletionDate(assessment, type, laaTransactionId);
        }
    }

    private void updateApplicationCompletionDate(MeansAssessmentDTO assessment, AssessmentType type, String laaTransactionId) {
        assessment.setDateCompleted(LocalDateTime.now());
        if (AssessmentType.FULL.equals(type)) {
            DateCompletionRequestDTO dateCompletionRequestDTO = DateCompletionRequestDTO
                    .builder()
                    .repId(assessment.getMeansAssessment().getRepId())
                    .assessmentDateCompleted(assessment.getDateCompleted())
                    .build();
            maatCourtDataService.updateCompletionDate(dateCompletionRequestDTO, laaTransactionId);
        }
    }

    private boolean isFullUpdateRequired(MeansAssessmentDTO assessment, String laaTransactionId) {
        int financialAssessmentId = assessment.getMeansAssessment().getFinancialAssessmentId();
        FinancialAssessmentDTO existingAssessment = maatCourtDataService.getFinancialAssessment(
                financialAssessmentId, laaTransactionId
        );
        return existingAssessment.getDateCompleted() == null;
    }

    private boolean isInitAssessmentComplete(MeansAssessmentDTO assessment) {
        InitAssessmentResult initResult = assessment.getInitAssessmentResult();
        if (InitAssessmentResult.PASS.equals(initResult)) {
            return true;
        } else if (InitAssessmentResult.FAIL.equals(initResult)) {
            CaseType caseType = assessment.getMeansAssessment().getCaseType();
            MagCourtOutcome magCourtOutcome = assessment.getMeansAssessment().getMagCourtOutcome();
            if (CaseType.SUMMARY_ONLY.equals(caseType) || CaseType.COMMITAL.equals(caseType)) {
                return true;
            } else {
                return CaseType.EITHER_WAY.equals(caseType) &&
                        MagCourtOutcome.COMMITTED_FOR_TRIAL.equals(magCourtOutcome);
            }
        }
        return false;
    }
}
