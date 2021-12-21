package uk.gov.justice.laa.crime.meansassessment.validator.initial;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentResponse;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitialMeansAssessmentValidationProcessor {

    private final CreateMeansAssessmentResponseValidator createMeansAssessmentResponseValidator;

    public Optional<Void> validate(ApiCreateMeansAssessmentResponse response){

        createMeansAssessmentResponseValidator.validate(response);

        return Optional.empty();
    }
}
