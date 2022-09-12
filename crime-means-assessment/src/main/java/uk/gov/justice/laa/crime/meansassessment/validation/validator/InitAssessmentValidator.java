package uk.gov.justice.laa.crime.meansassessment.validation.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitAssessmentValidator {

    public boolean validate(MeansAssessmentRequestDTO requestDTO) {
        boolean isValid = true;
        String refusedRepOrderDecision = "Refused - Ineligible";
        if ((requestDTO.getReviewType() == null)
                && (requestDTO.getCrownCourtOverview() != null
                && requestDTO.getCrownCourtOverview().getCrownCourtSummary() != null
                && requestDTO.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision() != null
                && refusedRepOrderDecision.equalsIgnoreCase(requestDTO.getCrownCourtOverview().getCrownCourtSummary().getRepOrderDecision()))) {

            isValid = false;
        }
        return isValid;
    }
}