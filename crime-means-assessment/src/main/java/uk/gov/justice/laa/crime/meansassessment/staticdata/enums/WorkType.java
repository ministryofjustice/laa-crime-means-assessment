package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkType {

    Initial_Assessment("Initial Assessment"),
    Full_Means_Test("Full Means Test"),
    Passported("Passported"),
    Hardship_Review_Magistrate("Hardship Review - Magistrate"),
    Hardship_Review_CrownCourt("Hardship Review - Crown Court"),
    IoJ_Appeal("IoJ Appeal");

    private String description;
}
