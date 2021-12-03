package uk.gov.justice.laa.crime.meansassessment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

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

    public static Frequency get(String code){
        return Stream.of(Frequency.values())
                .filter(f -> f.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}