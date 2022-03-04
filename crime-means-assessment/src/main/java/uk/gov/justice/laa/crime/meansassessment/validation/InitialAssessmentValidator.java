package uk.gov.justice.laa.crime.meansassessment.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.exception.MeansAssessmentValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitialAssessmentValidator {

    public void validate(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) throws MeansAssessmentValidationException {
        if( (apiCreateMeansAssessmentRequest.getReviewType() == null
                || apiCreateMeansAssessmentRequest.getReviewType().getCode() == null
                || apiCreateMeansAssessmentRequest.getReviewType().getCode().isEmpty()
        ) && (apiCreateMeansAssessmentRequest.getCrownCourtOverview() != null
                && apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary() !=null
                && apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision() !=null
                && "Refused - Ineligible".equalsIgnoreCase(apiCreateMeansAssessmentRequest.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision()) ))  {

            throw new MeansAssessmentValidationException("-20245,'Null mandatory fields'");
        }
    }
}