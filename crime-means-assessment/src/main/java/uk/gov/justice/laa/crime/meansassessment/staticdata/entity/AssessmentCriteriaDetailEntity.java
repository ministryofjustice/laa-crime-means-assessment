package uk.gov.justice.laa.crime.meansassessment.staticdata.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "ass_criteria_details", schema = "crime_means_assessment", uniqueConstraints = {
        @UniqueConstraint(name = "uk_acdt_seq_section", columnNames = {"ass_criteria_id", "seq", "section"})
})
public class AssessmentCriteriaDetailEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ass_criteria_id", referencedColumnName = "id", nullable = false)
    private AssessmentCriteriaEntity assessmentCriteria;

    @ManyToOne
    @JoinColumn(name = "asde_detail_code", referencedColumnName = "detail_code", nullable = false)
    private AssessmentDetailEntity assessmentDetail;

    @Column(name = "section", nullable = false)
    private String section;

    @Column(name = "seq", nullable = false)
    private Integer seq;

    @Column(name = "description", nullable = false)
    private String description;

    // TODO Check if this is ok or if we need to update data
    @Column(name = "use_frequency", nullable = true)
    private Boolean useFrequency;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "date_created", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(name = "modified_by", nullable = true)
    private String modifiedBy;

    @Column(name = "date_modified")
    @UpdateTimestamp
    private LocalDateTime modifiedDateTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessmentCriteriaDetail")
    @ToString.Exclude
    private Set<CaseTypeAssessmentCriteriaDetailValueEntity> caseTypeAssessmentDetailValues;
}