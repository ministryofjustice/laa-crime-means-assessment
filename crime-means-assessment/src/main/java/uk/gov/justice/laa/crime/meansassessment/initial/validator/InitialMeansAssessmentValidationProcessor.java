package uk.gov.justice.laa.crime.meansassessment.initial.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentResponse;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitialMeansAssessmentValidationProcessor {

    private final CreateMeansAssessmentResponseValidator createMeansAssessmentResponseValidator;

    public Optional<Void> validate(ApiCreateMeansAssessmentResponse response){
        //TODO: check if it is null?

        createMeansAssessmentResponseValidator.validate(response);

        return Optional.empty();
    }
}
