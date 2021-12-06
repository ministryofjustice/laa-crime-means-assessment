package uk.gov.justice.laa.crime.meansassessment.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * static data migrated from TOGDATA.FREQUENCIES table
 */
@AllArgsConstructor
@Getter
public enum Frequency {
    WEEKLY("WEEKLY", 52),
    TWO_WEEKLY("2WEEKLY", 26),
    FOUR_WEEKLY("4WEEKLY",13),
    MONTHLY("MONTHLY",12),
    ANNUALLY("ANNUALLY",1);

    private String code;
    private int weighting;

    @JsonValue
    public String getCode() {
        return code;
    }

    /***
     * Retrieve Frequency that maps Code. If code is null, returns null.
     * if code does not match Frequency.code, throws exception
     * @param code
     * @returns mapped Frequency.
     * @throws IllegalArgumentException
     */
    public static Frequency getFrom(String code) throws IllegalArgumentException{
        if (code == null) return null;

        return Stream.of(Frequency.values())
                .filter(f -> f.code.equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}