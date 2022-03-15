package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewType {

    ER("ER", "Eligibility Review"),
    EM("EM", "Eligibility Miscalculation Review"),
    NAFI("NAFI", "New Application Following Ineligibility");

    private String code;
    private String description;

}
