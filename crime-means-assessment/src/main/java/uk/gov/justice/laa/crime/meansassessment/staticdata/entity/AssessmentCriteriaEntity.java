package uk.gov.justice.laa.crime.meansassessment.staticdata.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "ASSESSMENT_CRITERIA", schema = "CRIME_MEANS_ASSESSMENT")
public class AssessmentCriteriaEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DATE_FROM", nullable = false)
    private LocalDateTime dateFrom;

    @Column(name = "DATE_TO")
    private LocalDateTime dateTo;    

    @Column(name = "INITIAL_LOWER_THRESHOLD", nullable = false)
    private BigDecimal initialLowerThreshold;
    
    @Column(name = "INITIAL_UPPER_THRESHOLD", nullable = false)
    private BigDecimal initialUpperThreshold; 
    
    @Column(name = "FULL_THRESHOLD", nullable = false)
    private BigDecimal fullThreshold;

    @Column(name = "APPLICANT_WEIGHTING_FACTOR", nullable = false)
    private BigDecimal applicantWeightingFactor;
    
    @Column(name = "PARTNER_WEIGHTING_FACTOR", nullable = false)
    private BigDecimal partnerWeightingFactor;

    @Column(name = "LIVING_ALLOWANCE", nullable = false)
    private BigDecimal livingAllowance;

    @Column(name = "ELIGIBILITY_THRESHOLD", nullable = false)
    private BigDecimal eligibilityThreshold;    

    @Column(name = "CREATED_BY", nullable = false)
    private String createdBy;    

    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(name = "MODIFIED_BY", nullable = false)
    private String modifiedBy;    

    @Column(name = "DATE_MODIFIED")
    @UpdateTimestamp
    private LocalDateTime modifiedDateTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessmentCriteria")
    private Set<AssessmentCriteriaChildWeightingEntity> assessmentCriteriaChildWeightings;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessmentCriteria")
    private Set<AssessmentCriteriaDetailEntity> assessmentCriteriaDetails;
}