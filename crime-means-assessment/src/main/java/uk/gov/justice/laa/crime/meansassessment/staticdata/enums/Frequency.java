package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

/**
 * static data migrated from TOGDATA.FREQUENCIES table
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum Frequency {
    WEEKLY("WEEKLY", 52),
    TWO_WEEKLY("2WEEKLY", 26),
    FOUR_WEEKLY("4WEEKLY", 13),
    MONTHLY("MONTHLY", 12),
    ANNUALLY("ANNUALLY", 1);

    @NotNull
    @JsonPropertyDescription("This will have the frequency code of the selection")
    private String code;
    private int weighting;

    @JsonValue
    public String getCode() {
        return code;
    }

    public static Frequency getFrom(String code) {
        if (StringUtils.isBlank(code)) return null;

        return Stream.of(Frequency.values())
                .filter(f -> f.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Frequency with value: %s does not exist.", code)));
    }
}