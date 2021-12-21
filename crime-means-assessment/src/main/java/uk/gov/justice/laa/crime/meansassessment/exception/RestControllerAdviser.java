package uk.gov.justice.laa.crime.meansassessment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentIdPresentException;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentInvalidIdException;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentMissingException;
import uk.gov.justice.laa.crime.meansassessment.defendant.exceptions.DefendantAssessmentNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;

@RestControllerAdvice
@Slf4j
public class RestControllerAdviser {

    @ExceptionHandler(DefendantAssessmentInvalidIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleInvalidDefendantAssessmentId(RuntimeException ex) {
        log.error("invalid defendant assessment id.", ex);
        return new ResponseEntity<>(ErrorDTO.builder().code(HttpStatus.BAD_REQUEST.toString()).message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefendantAssessmentIdPresentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleDefendantAssessmentIdPresent(RuntimeException ex) {
        log.error("Defendant Assessment id is present.", ex);
        return new ResponseEntity<>(ErrorDTO.builder().code(HttpStatus.BAD_REQUEST.toString()).message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DefendantAssessmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDTO> handleDefendantAssessmentNotFound(RuntimeException ex) {
        log.error("Defentant assessment not found.", ex);
        return new ResponseEntity<>(ErrorDTO.builder().code(HttpStatus.NOT_FOUND.toString()).message(ex.getMessage()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DefendantAssessmentMissingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDTO> handleDefendantAssessmentMissing(RuntimeException ex) {
        log.error("Defentant assessment missing in request body.", ex);
        return new ResponseEntity<>(ErrorDTO.builder().code(HttpStatus.BAD_REQUEST.toString()).message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDTO> handleDefendantAssessmentInternalServerError(RuntimeException ex) {
        log.error("Defentant assessment - Internal server error.", ex);
        return new ResponseEntity<>(ErrorDTO.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.toString()).message(ex.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
