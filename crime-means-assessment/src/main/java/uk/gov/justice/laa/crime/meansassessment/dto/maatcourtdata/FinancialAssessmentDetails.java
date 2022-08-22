package uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialAssessmentDetails {
    private Integer id;
    private Integer criteriaDetailId;
    private BigDecimal applicantAmount;
    private Frequency applicantFrequency;
    private BigDecimal partnerAmount;
    private Frequency partnerFrequency;
}
