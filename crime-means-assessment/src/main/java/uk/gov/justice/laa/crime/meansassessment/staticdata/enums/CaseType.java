package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.stream.Stream;

/**
 * static data migrated from TOGDATA.CASE_TYPES table
 */
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum CaseType {
    INDICTABLE("INDICTABLE", "Indictable", Boolean.TRUE),
    SUMMARY_ONLY("SUMMARY ONLY", "Summary-only", Boolean.TRUE),
    CC_ALREADY("CC ALREADY","Trial already in Crown Court", Boolean.TRUE),
    APPEAL_CC("APPEAL CC","Appeal to Crown Court", Boolean.FALSE),
    COMMITTAL("COMMITTAL","Committal for Sentence", Boolean.TRUE),
    EITHER_WAY("EITHER WAY","Either-Way", Boolean.FALSE);

    @NotNull
    @JsonPropertyDescription("Specifies the case type")
    private String caseType;
    private String description;
    private Boolean mcooOutcomeRequired;

    @JsonValue
    public String getCaseType() {
        return caseType;
    }

//    @JsonCreator
    public static CaseType getFrom(String caseType) throws IllegalArgumentException{
        if (StringUtils.isBlank(caseType)) return null;

        return Stream.of(CaseType.values())
                .filter(f -> f.caseType.equals(caseType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("CaseType with value: %s does not exist.", caseType)));
    }
}