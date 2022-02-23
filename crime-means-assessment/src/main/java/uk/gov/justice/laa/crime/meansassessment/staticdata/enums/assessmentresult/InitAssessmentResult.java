package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.assessmentresult;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InitAssessmentResult implements AssessmentResult {
    PASS(AssessmentResultCode.PASS, AssessmentResultType.INIT),
    FAIL(AssessmentResultCode.FAIL, AssessmentResultType.INIT),
    FULL(AssessmentResultCode.FULL, AssessmentResultType.INIT),
    HARDSHIP_APPLICATION(AssessmentResultCode.HARDSHIP_APPLICATION, AssessmentResultType.INIT);

    private final AssessmentResultCode code;
    private final AssessmentResultType assessmentResultType;

    @Override
    public AssessmentResultCode getCode() {
        return code;
    }

    @Override
    public AssessmentResultType getAssessmentResultType() {
        return assessmentResultType;
    }

}
