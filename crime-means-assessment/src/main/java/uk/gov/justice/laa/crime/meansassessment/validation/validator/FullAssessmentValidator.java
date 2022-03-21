package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class FullAssessmentValidator {

    public boolean validate(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) {
        return apiCreateMeansAssessmentRequest.getFullAssessmentDate() != null;
    }
}
