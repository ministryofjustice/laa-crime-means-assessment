package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentCriteriaDTO {

    private Integer id;

    private LocalDateTime dateFrom;

    private LocalDateTime dateTo;

    private BigDecimal initialLowerThreshold;

    private BigDecimal initialUpperThreshold;

    private BigDecimal fullThreshold;

    private BigDecimal applicantWeightingFactor;

    private BigDecimal partnerWeightingFactor;

    private BigDecimal livingAllowance;

    private BigDecimal eligibilityThreshold;

    private String createdBy;

    private LocalDateTime createdDateTime;

    private String modifiedBy;

    private LocalDateTime modifiedDateTime;

    private Set<AssessmentCriteriaChildWeightingEntity> assessmentCriteriaChildWeightings;

    private Set<AssessmentCriteriaDetailEntity> assessmentCriteriaDetails;
}