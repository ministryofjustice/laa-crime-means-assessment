package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ReviewType {

    ER("ER", "Eligibility Review"),
    EM("EM", "Eligibility Miscalculation Review"),
    NAFI("NAFI", "New Application Following Ineligibility");

    private String code;
    private String description;

    public static ReviewType getFrom(String code) throws IllegalArgumentException {
        return Stream.of(ReviewType.values())
                .filter(a -> a.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Review Type with value: %s does not exist.", code)));
    }
}
