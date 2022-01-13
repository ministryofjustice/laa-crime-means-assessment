package uk.gov.justice.laa.crime.meansassessment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;

@RestControllerAdvice
@Slf4j
public class RestControllerAdviser {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleRequestObjectValidation(MethodArgumentNotValidException ex) {
        log.error(" ERROR with request object validation: ", ex);
        return getNewErrorResponseWith(HttpStatus.BAD_REQUEST, ex.getBindingResult() == null ? ex.getMessage(): String.valueOf(ex.getBindingResult()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDTO> handleDefendantAssessmentInternalServerError(RuntimeException ex) {
        log.error("Defendant assessment - Internal server error.", ex);
        return getNewErrorResponseWith(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private static ResponseEntity<ErrorDTO> getNewErrorResponseWith(HttpStatus status, String errorMessage){
        return new ResponseEntity<>(ErrorDTO.builder().code(status.toString()).message(errorMessage).build(), status);
    }
}
