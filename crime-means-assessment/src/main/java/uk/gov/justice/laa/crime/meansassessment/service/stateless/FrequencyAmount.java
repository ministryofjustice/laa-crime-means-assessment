package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class FrequencyAmount {
    @Valid
    @NotNull
    @JsonProperty("frequency")
    private Frequency frequency;

    @NotNull
    @JsonProperty("amount")
    private BigDecimal amount;
}

