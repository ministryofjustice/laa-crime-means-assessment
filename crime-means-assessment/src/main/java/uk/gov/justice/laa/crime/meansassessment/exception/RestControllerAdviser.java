package uk.gov.justice.laa.crime.meansassessment.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    public ResponseEntity<ErrorDTO> adviceBadRequests(MethodArgumentNotValidException ex) {
        log.error("Bad request is passed in: ", ex);
        return getNewErrorResponseWith(HttpStatus.BAD_REQUEST, String.valueOf(ex.getBindingResult()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> adviceParseError(HttpMessageNotReadableException ex) {
        log.error("Failed to parse request JSON: ", ex);
        return getNewErrorResponseWith(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(APIClientException.class)
    public ResponseEntity<ErrorDTO> handleApiClientError(APIClientException ex) {
        log.error("APIClientException: ", ex);
        return getNewErrorResponseWith(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationError(ValidationException ex) {
        log.error("ValidationException: ", ex);
        return getNewErrorResponseWith(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDTO> adviceServiceErrors(RuntimeException ex) {
        log.error("Service is failed due to in internal error.", ex);
        return getNewErrorResponseWith(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private static ResponseEntity<ErrorDTO> getNewErrorResponseWith(HttpStatus status, String errorMessage){
        return new ResponseEntity<>(ErrorDTO.builder().code(status.toString()).message(errorMessage).build(), status);
    }
}
