package uk.gov.justice.laa.crime.meansassessment.defendant.exceptions;

public class DefendantAssessmentNotFoundException extends RuntimeException{
    public DefendantAssessmentNotFoundException(String defendantAssessmentId){
        super(new StringBuilder("Defendant Assessment not found. ID: ").append(defendantAssessmentId).toString());
    }
}
