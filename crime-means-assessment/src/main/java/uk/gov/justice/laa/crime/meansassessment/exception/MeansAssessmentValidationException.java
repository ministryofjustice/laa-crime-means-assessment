package uk.gov.justice.laa.crime.meansassessment.exception;


import java.io.Serializable;

public class MeansAssessmentValidationException extends Exception implements Serializable {

    public MeansAssessmentValidationException(String message) {
        super(message);
    }
}
