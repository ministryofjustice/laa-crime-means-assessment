package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.justice.laa.crime.meansassessment.StatelessFullResult;
import uk.gov.justice.laa.crime.meansassessment.StatelessInitialResult;

@AllArgsConstructor
@Getter
public class StatelessResult {
    private final StatelessFullResult fullResult;
    private final StatelessInitialResult initialResult;
}
