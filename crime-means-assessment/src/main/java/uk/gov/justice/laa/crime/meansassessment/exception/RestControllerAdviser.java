package uk.gov.justice.laa.crime.meansassessment.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.gov.justice.laa.crime.dto.ErrorDTO;
import uk.gov.justice.laa.crime.meansassessment.tracing.TraceIdHandler;

import java.io.IOException;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class RestControllerAdviser {

    private final TraceIdHandler traceIdHandler;
    private final ObjectMapper mapper;

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorDTO> onRuntimeException(WebClientResponseException exception) {
        String errorMessage;
        try {
            ErrorDTO errorDTO = mapper.readValue(exception.getResponseBodyAsString(), ErrorDTO.class);
            errorMessage = errorDTO.getMessage();
        } catch (IOException ex) {
            log.warn("Unable to read the ErrorDTO from WebClientResponseException", ex);
            errorMessage = exception.getMessage();
        }
        return getNewErrorResponseWith(exception.getStatusCode(), errorMessage, traceIdHandler.getTraceId());
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ErrorDTO> onRuntimeException(WebClientRequestException exception) {
        return getNewErrorResponseWith(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), traceIdHandler.getTraceId());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> adviceBadRequests(MethodArgumentNotValidException ex) {
        log.error("Bad request is passed in: ", ex);
        return getNewErrorResponseWith(HttpStatus.BAD_REQUEST, String.valueOf(ex.getBindingResult()), traceIdHandler.getTraceId());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> adviceParseError(HttpMessageNotReadableException ex) {
        log.error("Failed to parse request JSON: ", ex);
        return getNewErrorResponseWith(HttpStatus.BAD_REQUEST, ex.getMessage(), traceIdHandler.getTraceId());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationError(ValidationException ex) {
        log.warn("ValidationException: ", ex);
        return getNewErrorResponseWith(HttpStatus.BAD_REQUEST, ex.getMessage(), traceIdHandler.getTraceId());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> adviceServiceErrors(RuntimeException ex) {
        log.error("Service is failed due to in internal error.", ex);
        return getNewErrorResponseWith(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), traceIdHandler.getTraceId());
    }

    private static ResponseEntity<ErrorDTO> getNewErrorResponseWith(HttpStatusCode status, String errorMessage, String traceId) {
        return new ResponseEntity<>(ErrorDTO.builder().traceId(traceId).code(status.toString()).message(errorMessage).build(), status);
    }

}
