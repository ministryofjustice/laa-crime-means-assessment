package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.Assessment;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.RepOrderDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrownCourtEligibilityService {

    private final MaatCourtDataService maatCourtDataService;

    public boolean isEligibilityCheckRequired(MeansAssessmentRequestDTO assessmentRequest) {

        if (!hasRequiredCaseTypeAndOutcome(assessmentRequest)) {
            return false;
        }

        String laaTransactionId = assessmentRequest.getLaaTransactionId();

        boolean isEitherWayAndCommittedForTrial =
                CaseType.EITHER_WAY.equals(assessmentRequest.getCaseType())
                        && MagCourtOutcome.COMMITTED_FOR_TRIAL.equals(assessmentRequest.getMagCourtOutcome());
        RepOrderDTO repOrder = maatCourtDataService.getRepOrder(assessmentRequest.getRepId(), laaTransactionId);

        if (isEitherWayAndCommittedForTrial) {
            Integer financialAssessmentId = assessmentRequest.getFinancialAssessmentId();
            FinancialAssessmentDTO initialAssessment = repOrder.getFinancialAssessments().stream()
                    .filter(assessment -> assessment.getId().equals(financialAssessmentId)
                    ).findFirst().orElseThrow(
                            () -> new RuntimeException(
                                    String.format("Cannot find initial assessment with id: %s", financialAssessmentId)
                            )
                    );
            boolean isFirstMeansAssessment =
                    NewWorkReason.FMA.equals(NewWorkReason.getFrom(initialAssessment.getNewWorkReason()));

            if (!isFirstMeansAssessment) {
                boolean isInitResultPass =
                        InitAssessmentResult.PASS.equals(InitAssessmentResult.getFrom(initialAssessment.getInitResult()));
                boolean isDateCreatedAfterMagsOutcome =
                        initialAssessment.getDateCreated().isAfter(repOrder.getMagsOutcomeDateSet());

                if (isInitResultPass || isDateCreatedAfterMagsOutcome) {
                    Assessment previousAssessment = getLatestAssessment(repOrder, financialAssessmentId);
                    if (previousAssessment != null) {
                        return !hasDisqualifyingResult(previousAssessment);
                    }
                }
                return true;
            }
        }
        Stream<Assessment> previousAssessments = getPreviousAssessments(repOrder);
        return previousAssessments
                .noneMatch(this::hasDisqualifyingResult);
    }

    private boolean hasRequiredCaseTypeAndOutcome(MeansAssessmentRequestDTO assessmentRequest) {
        CaseType caseType = assessmentRequest.getCaseType();
        MagCourtOutcome magCourtOutcome = assessmentRequest.getMagCourtOutcome();
        return ((caseType == CaseType.INDICTABLE || caseType == CaseType.CC_ALREADY)
                && magCourtOutcome == MagCourtOutcome.SENT_FOR_TRIAL) ||
                caseType == CaseType.EITHER_WAY && magCourtOutcome == MagCourtOutcome.COMMITTED_FOR_TRIAL;
    }

    private Stream<Assessment> getPreviousAssessments(RepOrderDTO repOrder) {
        return Stream.of(repOrder.getPassportAssessments(), repOrder.getFinancialAssessments())
                .flatMap(Collection::stream);
    }

    private Assessment getLatestAssessment(RepOrderDTO repOrder, Integer financialAssessmentId) {
        return getPreviousAssessments(repOrder)
                .filter(assessment -> !financialAssessmentId.equals(assessment.getId()))
                .max(comparing(Assessment::getDateCreated)).orElse(null);
    }

    private boolean hasDisqualifyingResult(Assessment assessment) {
        if (assessment instanceof FinancialAssessmentDTO) {
            FinancialAssessmentDTO means = (FinancialAssessmentDTO) assessment;
            return InitAssessmentResult.PASS.equals(InitAssessmentResult.getFrom(means.getInitResult()))
                    || FullAssessmentResult.PASS.equals(FullAssessmentResult.getFrom(means.getFullResult()))
                    || FullAssessmentResult.FAIL.equals(FullAssessmentResult.getFrom(means.getFullResult()));
        } else {
            PassportAssessmentDTO passport = (PassportAssessmentDTO) assessment;
            return PassportAssessmentResult.PASS.equals(PassportAssessmentResult.getFrom(passport.getResult()));
        }
    }
}
