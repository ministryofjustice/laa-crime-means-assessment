package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.assessmentresult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AssessmentResultCode {
    PASS("PASS", "Pass"),
    FAIL("FAIL", "Fail"),
    FULL("FULL", "Full"),
    HARDSHIP_APPLICATION("HARDSHIP_APPLICATION", "Hardship application"),
    FAIL_CONTINUE("FAIL CONTINUE", "Fail-Benefits Bypass"),
    TEMP("TEMP", "Temporary Pass"),
    INEL("INEL", "FAILED ELIGIBILTY TEST");

    private final String code;
    private final String description;

}
