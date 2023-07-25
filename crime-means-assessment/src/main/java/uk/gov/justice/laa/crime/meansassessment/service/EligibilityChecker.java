package uk.gov.justice.laa.crime.meansassessment.service;

import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Client;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.MagCourtOutcome;

public interface EligibilityChecker {
    static boolean isCrownCourtCase(CaseType caseType, MagCourtOutcome magCourtOutcome) {
        return ((caseType == CaseType.INDICTABLE || caseType == CaseType.CC_ALREADY)
                && magCourtOutcome == MagCourtOutcome.SENT_FOR_TRIAL) ||
                caseType == CaseType.EITHER_WAY && magCourtOutcome == MagCourtOutcome.COMMITTED_FOR_TRIAL;
    }

    boolean isEligibilityCheckRequired(MeansAssessmentRequestDTO requestDTO);

    Client getCheckerByClientName();
}
