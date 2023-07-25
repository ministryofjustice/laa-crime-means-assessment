package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.service.EligibilityChecker;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Client;

@Service
@RequiredArgsConstructor
public class DefaultEligibilityService implements EligibilityChecker {
    @Override
    public boolean isEligibilityCheckRequired(MeansAssessmentRequestDTO requestDTO) {
        return true;
    }

    @Override
    public Client getCheckerByClientName() {
        return Client.CFE;
    }
}
