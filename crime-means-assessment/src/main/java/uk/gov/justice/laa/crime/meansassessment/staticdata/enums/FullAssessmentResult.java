package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FullAssessmentResult {
    NONE,
    PASS("PASS", "Gross income below the threshold"),
    FAIL("FAIL", "Gross income above the threshold");

    private String result;
    private String reason;

    public static FullAssessmentResult getFrom(String result) throws IllegalArgumentException {
        if (StringUtils.isBlank(result)) return null;
        // TODO: Fix NullPointerException
        return Stream.of(FullAssessmentResult.values())
                .filter(a -> a.result.equals(result))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Full Assessment Result with value: %s does not exist.", result)));
    }
}
