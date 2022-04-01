package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitAssessmentValidator {

    public boolean validate(MeansAssessmentRequestDTO requestDTO) {
        boolean isValid = true;
        String REFUSED_REP_ORDER_DECISION = "Refused - Ineligible";
        if ((requestDTO.getReviewType() == null)
                && (requestDTO.getCrownCourtOverview() != null
                && requestDTO.getCrownCourtOverview().getCrownCourtSummary() != null
                && requestDTO.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision() != null
                && REFUSED_REP_ORDER_DECISION.equalsIgnoreCase(requestDTO.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision()))) {

            isValid = false;
        }
        return isValid;
    }
}