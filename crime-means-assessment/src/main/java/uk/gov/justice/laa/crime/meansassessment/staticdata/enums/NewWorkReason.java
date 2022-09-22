package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum NewWorkReason {

    PBI("PBI", "ASS", "Crown Court Section 51"),
    PCI("PCI", "ASS", "Crown Court either way offence"),
    FMA("FMA", "ASS", "First Means Assessment"),
    PAI("PAI", "ASS", "Previous Assessment was Incorrect"),
    CFC("CFC", "ASS", "Change in Financial Circumstances"),
    CPS("CPS", "ASS", "Change in Solicitor"),
    HR("HR", "ASS", "Hardship Review (NCT only)"),
    NEW("NEW", "HARDIOJ", "New"),
    PRI("PRI", "HARDIOJ", "Previous Record Incorrect"),
    JR("JR", "HARDIOJ", "Judicial Review"),
    EVI("EVI", "ASS", "Income Evidence Differs from Declaration"),
    INF("INF", "ASS", "Re-assessment Following New Information"),
    CSP("CSP", "ASS", "Change in Partner Status");

    private String code;
    private String type;
    private String description;

    public static NewWorkReason getFrom(String code) {
        if (StringUtils.isBlank(code)) return null;

        return Stream.of(NewWorkReason.values())
                .filter(newWorkReason -> newWorkReason.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("New Work Reason with value: %s does not exist.", code)));
    }
}
