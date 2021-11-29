package uk.gov.justice.laa.crime.meansassessment.defendant.exceptions;

public class DefendantAssessmentMissingException extends RuntimeException{
    public DefendantAssessmentMissingException(){
        super("Defendant Assessment missing in request body ");
    }
}
