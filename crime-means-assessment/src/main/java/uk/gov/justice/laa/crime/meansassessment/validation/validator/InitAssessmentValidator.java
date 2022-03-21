package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitAssessmentValidator {

    public boolean validate(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) {
        boolean isValid = true;
        String REFUSED_REP_ORDER_DECISION = "Refused - Ineligible";
        if ((apiCreateMeansAssessmentRequest.getReviewType() == null)
                && (apiCreateMeansAssessmentRequest.getCrownCourtOverview() != null
                && apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary() != null
                && apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision() != null
                && REFUSED_REP_ORDER_DECISION.equalsIgnoreCase(apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision()))) {

            isValid = false;
        }
        return isValid;
    }
}