package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.assessmentresult;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FullAssessmentResult implements AssessmentResult {

    PASS(AssessmentResultCode.PASS, AssessmentResultType.FULL),
    FAIL(AssessmentResultCode.FAIL, AssessmentResultType.FULL),
    INEL(AssessmentResultCode.INEL, AssessmentResultType.FULL);

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
