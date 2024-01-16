package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.enums.AssessmentType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class AssessmentSectionSummaryDTO {

    private String section;
    private BigDecimal applicantAnnualTotal;
    private BigDecimal partnerAnnualTotal;
    private BigDecimal annualTotal;
    private AssessmentType assessmentType;
    private final List<AssessmentDetailDTO> assessmentDetails = new ArrayList<>();
}
