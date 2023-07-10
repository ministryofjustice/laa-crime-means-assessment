package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class StatelessInitialResult {
    private final InitAssessmentResult result;
    private final BigDecimal lowerThreshold;
    private final BigDecimal upperThreshold;
    private final boolean fullAssessmentPossible;

    public String getResultReason() {
        return result.getReason();
    }
}

