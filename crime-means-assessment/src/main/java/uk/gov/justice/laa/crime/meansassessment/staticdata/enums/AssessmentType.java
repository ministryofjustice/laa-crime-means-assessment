package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AssessmentType {
    INIT, FULL;

    @NotNull
    @JsonPropertyDescription("Determines the assessment type")
    private String type;

    @JsonValue
    public String getCode() {
        return type;
    }

    public static AssessmentType getFrom(String type) throws IllegalArgumentException {
        if (StringUtils.isBlank(type)) return null;

        return Stream.of(AssessmentType.values())
                .filter(f -> f.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Assessment type with value: %s does not exist.", type)));
    }
}
