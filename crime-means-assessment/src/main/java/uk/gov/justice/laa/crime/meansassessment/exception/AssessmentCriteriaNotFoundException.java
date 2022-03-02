package uk.gov.justice.laa.crime.meansassessment.exception;

import java.io.Serializable;

public class AssessmentCriteriaNotFoundException extends RuntimeException implements Serializable {

    private static final String DEFAULT_MESSAGE = "Assessment Criteria not found for given assessment date";

    public AssessmentCriteriaNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public AssessmentCriteriaNotFoundException(String message) {
        super(message);
    }

    public AssessmentCriteriaNotFoundException(Throwable cause) {
        super(cause);
    }

    public AssessmentCriteriaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
