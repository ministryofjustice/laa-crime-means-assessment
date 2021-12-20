package uk.gov.justice.laa.crime.meansassessment.initial.validator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.validator.IValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Component
public class CreateMeansAssessmentResponseValidator implements IValidator<Void, ApiCreateMeansAssessmentResponse> {

    @Override
    public Optional<Void> validate(ApiCreateMeansAssessmentResponse createMeansAssessmentResponse) throws ValidationException {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ApiCreateMeansAssessmentResponse>> constraintViolations = validator.validate(createMeansAssessmentResponse);
        if(constraintViolations.size() > 0) {

            throw new ValidationException(
                    new StringBuilder("Error validating ").append(ApiCreateMeansAssessmentResponse.class)
                            .append(
                                    constraintViolations.stream().map(this::getErrorMessageFrom).reduce("Errors: ", String::concat)).toString()
            );
        }
        return Optional.empty();
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
