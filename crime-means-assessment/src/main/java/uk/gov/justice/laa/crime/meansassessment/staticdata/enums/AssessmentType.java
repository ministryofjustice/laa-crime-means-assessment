package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum AssessmentType {
    INIT("INIT"), FULL("FULL");

    @NotNull
    @JsonPropertyDescription("Determines the assessment type")
    private String type;

    @JsonValue
    public String getType() {
        return type;
    }

    public static AssessmentType getFrom(String type)  {
        if (StringUtils.isBlank(type)) return null;

        return Stream.of(AssessmentType.values())
                .filter(f -> f.type.equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Assessment type with value: %s does not exist.", type)));
    }
}
