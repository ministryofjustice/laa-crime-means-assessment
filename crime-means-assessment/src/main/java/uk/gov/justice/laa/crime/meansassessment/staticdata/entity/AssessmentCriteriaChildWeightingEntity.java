package uk.gov.justice.laa.crime.meansassessment.staticdata.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "ASS_CRITERIA_CHILD_WEIGHTINGS", schema = "CRIME_MEANS_ASSESSMENT")
public class AssessmentCriteriaChildWeightingEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ass_criteria_id", nullable = false)
    private AssessmentCriteriaEntity assessmentCriteria;

    @Column(name = "lower_age_range", nullable = false)
    private Integer lowerAgeRange;

    @Column(name = "upper_age_range", nullable = false)
    private Integer upperAgeRange;

    @Column(name = "weighting_factor", nullable = false)
    private BigDecimal weightingFactor;

    @Column(name = "date_created", nullable = false, updatable = false)
    @CreationTimestamp
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdDateTime;

    @Column(name = "user_created", nullable = false)
    private String userCreated;

    @Column(name = "date_modified")
    @UpdateTimestamp
    @EqualsAndHashCode.Exclude
    private LocalDateTime modifiedDateTime;

    @Column(name = "user_modified")
    private String userModified;
}
