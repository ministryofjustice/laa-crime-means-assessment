package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;

import static uk.gov.justice.laa.crime.enums.MagCourtOutcome.COMMITTED_FOR_TRIAL;
import static uk.gov.justice.laa.crime.enums.NewWorkReason.HR;

@Slf4j
@Service
public class FullAssessmentAvailabilityService {

    private static boolean checkNewWorkReason(NewWorkReason newWorkReason) {
        return (newWorkReason == HR);
    }

    private static boolean isFullAssessmentAvailableOnResultFail(CaseType caseType, MagCourtOutcome magCourtOutcome) {
        switch (caseType) {
            case COMMITAL:
            case SUMMARY_ONLY:
                return false;
            case INDICTABLE:
            case CC_ALREADY:
            case APPEAL_CC:
                return true;
            case EITHER_WAY:
                return checkMagCourtOutcome(magCourtOutcome);
            default:
                return false;
        }
    }

    private static boolean checkMagCourtOutcome(MagCourtOutcome magCourtOutcome) {
        return (magCourtOutcome == COMMITTED_FOR_TRIAL);
    }

    public boolean isFullAssessmentAvailable(final CaseType caseType,
                                             final MagCourtOutcome magCourtOutcome,
                                             final NewWorkReason newWorkReason,
                                             final InitAssessmentResult initAssessmentResult) {
        var returnValue = false;

        if (initAssessmentResult != null) {
            switch (initAssessmentResult) {
                case PASS:
                    break;
                case FULL:
                    returnValue = true;
                    break;
                case FAIL:
                    returnValue = isFullAssessmentAvailableOnResultFail(caseType, magCourtOutcome);
                    break;
                case HARDSHIP:
                    returnValue = checkNewWorkReason(newWorkReason);
            }
        }

        return returnValue;
    }
}
