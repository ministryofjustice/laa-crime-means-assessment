package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum NewWorkReason {

    PBI("PBI", "Crown Court Section 51"),
    PCI("PCI", "Crown Court either way offence"),
    FMA("FMA", "First Means Assessment"),
    PAI("PAI", "Previous Assessment was Incorrect"),
    CFC("CFC", "Change in Financial Circumstances"),
    CPS("CPS", "Change in Solicitor"),
    HR("HR", "Hardship Review (NCT only)"),
    NEW("NEW", "New"),
    PRI("PRI", "Previous Record Incorrect"),
    JR("JR", "Judicial Review"),
    EVI("EVI", "Income Evidence Differs from Declaration"),
    INF("INF", "Re-assessment Following New Information"),
    CSP("CSP", "Change in Partner Status");

    private String code;
    private String description;

    public static NewWorkReason getFrom(String code) {
        if (StringUtils.isBlank(code)) return null;

        return Stream.of(NewWorkReason.values())
                .filter(newWorkReason -> newWorkReason.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("New Work Reason with value: %s does not exist.", code)));
    }
}
