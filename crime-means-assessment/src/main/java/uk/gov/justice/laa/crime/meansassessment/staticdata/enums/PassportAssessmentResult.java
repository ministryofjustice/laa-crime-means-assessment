package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PassportAssessmentResult {
    PASS("PASS", "Gross income below the threshold"),
    FAIL("FAIL", "Gross income above the threshold");

    private String result;
    private String reason;

    public static PassportAssessmentResult getFrom(String result) {
        if (StringUtils.isBlank(result)) return null;

        return Stream.of(PassportAssessmentResult.values())
                .filter(p -> p.result.equals(result))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Passport Assessment Result with value: %s does not exist.", result)));
    }
}
