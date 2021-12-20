package uk.gov.justice.laa.crime.meansassessment.validator;

import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;

import java.util.Optional;

public interface IValidator <T, V> {
    Optional<T> validate(final V value) throws ValidationException;
}