package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDetailDTO {

    private Integer id;
    private Integer criteriaDetailId;
    private BigDecimal applicantAmount;
    private BigDecimal partnerAmount;
    private String assessmentDescription;
    private String assessmentDetailCode;
    private LocalDateTime dateModified;
    private Frequency applicantFrequency;
    private Frequency partnerFrequency;
}
