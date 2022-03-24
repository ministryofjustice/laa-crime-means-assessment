package uk.gov.justice.laa.crime.meansassessment.exception;

public class AssessmentProcessingException extends RuntimeException {

    /**
     * Constructs an instance of <code>AssessmentProcessingException</code>.
     */
    public AssessmentProcessingException() {
        super();
    }

    /**
     * Constructs an instance of <code>ValidationException</code> with
     * the specified detail message.
     *
     * @param message The detail message.
     */
    public AssessmentProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs an instance of <code>AssessmentProcessingException</code> with
     * the specified root cause.
     *
     * @param rootCause The root cause of this exception
     */
    public AssessmentProcessingException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * Constructs an instance of <code>AssessmentProcessingException</code> with
     * the specified root cause and detail message.
     *
     * @param message   The detail message.
     * @param rootCause The root cause of this exception
     */
    public AssessmentProcessingException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
