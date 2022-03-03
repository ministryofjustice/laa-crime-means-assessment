package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InitAssessmentResult {
    PASS("PASS", "Pass"),
    FAIL("FAIL", "Fail"),
    FULL("FULL", "Full"),
    HARDSHIP_APPLICATION("HARDSHIP_APPLICATION", "Hardship application");

    private final String code;
    private final String description;

    public String getCode() {
        return code;
    }

}
