package uk.gov.justice.laa.crime.meansassessment.initial.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import uk.gov.justice.laa.crime.meansassessment.dto.ErrorDTO;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

//@RestControllerAdvice
@Slf4j
public class RestPostProcessingAdvice  {//implements ResponseBodyAdvice<Object> {

    private Validator validator= Validation.buildDefaultValidatorFactory().getValidator();




//    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("In supports() method of " + getClass().getSimpleName());
        String containingClass =returnType.getContainingClass().toString();
        String parameterType = returnType.getParameterType().toString();

        return returnType.getContainingClass() == InitialMeansAssessmentController.class && returnType.getParameterType() == ResponseEntity.class;
    }

//    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(body);
        if(constraintViolations.size() > 0) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return
            ErrorDTO.builder().code(HttpStatus.BAD_REQUEST.toString()).message(constraintViolations.stream().map(this::getErrorMessageFrom).reduce("Errors: ", String::concat)).build();
//            for (var cv : constraintViolations  ) {
//                String propertyPath = cv.getPropertyPath().toString();
//                String interpolatedMessage = cv.getMessage();
//                String beanClassName = cv.getRootBean().getClass().getSimpleName();
//                String hello = "";
//            }
        }
//        new ResponseEntity<>(ErrorDTO.builder().code(HttpStatus.BAD_REQUEST.toString()).message(ex.getMessage()).build(), HttpStatus.BAD_REQUEST);

        return body;
    }
    private  String getErrorMessageFrom(ConstraintViolation cv){
        StringBuilder errorMessage = new StringBuilder("Error: ");
        errorMessage.append(cv.getRootBean().getClass().getSimpleName())
                .append(".")
                .append(cv.getPropertyPath().toString())
                .append(": ")
                .append(cv.getMessage())
                .append(".   ");
        return errorMessage.toString();
    }

}
