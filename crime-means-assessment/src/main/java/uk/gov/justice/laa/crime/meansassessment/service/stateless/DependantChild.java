package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;

@AllArgsConstructor
@Getter
public class DependantChild {
    @Valid
    @NotNull
    private AgeRange ageRange;

    @Valid
    @NotNull
    private Integer count;
}

