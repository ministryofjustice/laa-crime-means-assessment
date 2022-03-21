package uk.gov.justice.laa.crime.meansassessment.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class FullAssessmentValidator {

    public void validate(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) {
        if (apiCreateMeansAssessmentRequest.getFullAssessmentDate() == null) {
            throw new ValidationException("Null mandatory fields");
        }
    }
}
