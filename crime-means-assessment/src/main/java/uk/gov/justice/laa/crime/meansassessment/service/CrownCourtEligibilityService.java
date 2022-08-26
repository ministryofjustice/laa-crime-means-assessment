package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.Assessment;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.RepOrderDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrownCourtEligibilityService {

    private final MaatCourtDataService maatCourtDataService;

    public boolean isEligible(BigDecimal disposableIncome, MeansAssessmentRequestDTO assessmentRequest,
                              AssessmentCriteriaEntity assessmentCriteria) {
        if (isEligibilityCheckRequired(assessmentRequest)) {
            return disposableIncome.compareTo(assessmentCriteria.getEligibilityThreshold()) < 0;
        }
        return true;
    }

    boolean isEligibilityCheckRequired(MeansAssessmentRequestDTO assessmentRequest) {
        boolean isCheckRequired = true;
        String laaTransactionId = assessmentRequest.getLaaTransactionId();

        RepOrderDTO repOrder = maatCourtDataService.getRepOrder(assessmentRequest.getRepId(), laaTransactionId);
        List<PassportAssessmentDTO> passportAssessments =
                maatCourtDataService.getPassportAssessmentsFromRepId(assessmentRequest.getRepId(), laaTransactionId);

        FinancialAssessmentDTO initialAssessment = repOrder.getFinancialAssessments().stream().filter(
                assessment -> assessment.getId().equals(assessmentRequest.getFinancialAssessmentId())
        ).findFirst().orElseThrow(() -> new RuntimeException("Cannot find initial assessment"));

        boolean isFirstMeansAssessment =
                NewWorkReason.FMA.equals(NewWorkReason.getFrom(initialAssessment.getNewWorkReason()));

        boolean isEitherWayAndCommittedForTrial = CaseType.EITHER_WAY.equals(assessmentRequest.getCaseType())
                && MagCourtOutcome.COMMITTED_FOR_TRIAL.equals(assessmentRequest.getMagCourtOutcome());

        if (isFirstMeansAssessment && isEitherWayAndCommittedForTrial) {

            boolean isInitResultPass =
                    InitAssessmentResult.PASS.equals(InitAssessmentResult.getFrom(initialAssessment.getInitResult()));

            boolean isDateCreatedAfterMagsOutcome =
                    initialAssessment.getDateCreated().toLocalDate().isBefore(repOrder.getMagsOutcomeDateSet());

            List<Assessment> combinedAssessments = Stream.of(passportAssessments, repOrder.getFinancialAssessments())
                    .flatMap(Collection::stream).collect(Collectors.toList());

            if (isInitResultPass || isDateCreatedAfterMagsOutcome) {
                Assessment previous = combinedAssessments.stream()
                        .max(comparing(Assessment::getDateCreated)).orElse(null);

                if (previous instanceof FinancialAssessmentDTO) {
                    FinancialAssessmentDTO means = (FinancialAssessmentDTO) previous;
                    if (InitAssessmentResult.PASS.equals(InitAssessmentResult.getFrom(means.getInitResult()))
                            || FullAssessmentResult.PASS.equals(FullAssessmentResult.getFrom(means.getFullResult()))
                            || FullAssessmentResult.FAIL.equals(FullAssessmentResult.getFrom(means.getFullResult()))) {
                        isCheckRequired = false;
                    }
                } else if (previous instanceof PassportAssessmentDTO) {
                    PassportAssessmentDTO passport = (PassportAssessmentDTO) previous;
                    if (PassportAssessmentResult.PASS.equals(PassportAssessmentResult.getFrom(passport.getResult()))) {
                        isCheckRequired = false;
                    }
                }
            }
        }
        return isCheckRequired;
    }
}
