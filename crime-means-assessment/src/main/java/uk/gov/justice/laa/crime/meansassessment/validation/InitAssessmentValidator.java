package uk.gov.justice.laa.crime.meansassessment.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitAssessmentValidator {

    public void validate(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) {
        if ((apiCreateMeansAssessmentRequest.getReviewType() == null
                || apiCreateMeansAssessmentRequest.getReviewType().getCode() == null
                || apiCreateMeansAssessmentRequest.getReviewType().getCode().isEmpty()
        ) && (apiCreateMeansAssessmentRequest.getCrownCourtOverview() != null
                && apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary() != null
                && apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision() != null
                && "Refused - Ineligible".equalsIgnoreCase(apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision()))) {

            throw new ValidationException("Null mandatory fields");
        }
    }
}