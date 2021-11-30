package uk.gov.justice.laa.crime.meansassessment.defendant.exceptions;

public class DefendantAssessmentInvalidIdException extends RuntimeException{
    public DefendantAssessmentInvalidIdException(String defendantAssessmentId){
        super(new StringBuilder("Invalid Defendant Assessment ID: ").append(defendantAssessmentId).toString());
    }
}
