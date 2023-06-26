package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StatelessResult {
    private final StatelessFullResult fullResult;
    private final StatelessInitialResult initialResult;
}
