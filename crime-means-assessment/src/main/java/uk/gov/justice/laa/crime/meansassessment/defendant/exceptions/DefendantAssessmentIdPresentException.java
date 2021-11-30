package uk.gov.justice.laa.crime.meansassessment.defendant.exceptions;

public class DefendantAssessmentIdPresentException extends RuntimeException{
    public DefendantAssessmentIdPresentException(String defendantAssessmentId){
        super(new StringBuilder("Defendant Assessment is present. id: ").append(defendantAssessmentId).toString());
    }
}
