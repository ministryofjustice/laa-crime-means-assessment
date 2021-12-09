package uk.gov.justice.laa.crime.meansassessment.defendant.exceptions;

public class ObjectNotFoundException  extends RuntimeException {
    public ObjectNotFoundException(String objectId, String className){
        super(new StringBuilder(className).append(" not found. ID: ").append(objectId).toString());
    }
}
