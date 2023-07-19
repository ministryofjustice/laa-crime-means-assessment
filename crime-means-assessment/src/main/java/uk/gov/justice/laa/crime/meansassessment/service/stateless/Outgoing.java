package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.OutgoingType;

@Getter
@AllArgsConstructor
public class Outgoing implements Amount {
    @Valid
    @NotNull
    @JsonProperty("outgoing_type")
    private OutgoingType outgoingType;

    @Valid
    @NotNull
    @JsonProperty("applicant")
    private FrequencyAmount applicant;

    @Valid
    @JsonProperty("partner")
    private FrequencyAmount partner;
}

