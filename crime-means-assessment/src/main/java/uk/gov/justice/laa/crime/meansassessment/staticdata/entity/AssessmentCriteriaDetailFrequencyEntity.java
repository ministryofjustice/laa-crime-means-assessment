package uk.gov.justice.laa.crime.meansassessment.staticdata.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "ass_criteria_detail_freq", schema = "crime_means_assessment",
        uniqueConstraints = {@UniqueConstraint(name = "uk_acdf_acrdid_freqcode", columnNames = { "acrd_id", "freq_code" })})
public class AssessmentCriteriaDetailFrequencyEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "acrd_id", referencedColumnName = "id", nullable = false)
    private AssessmentCriteriaDetailEntity assessmentCriteriaDetail;

    @Column(name = "freq_code", nullable = false)
    private Frequency frequency;

    @Column(name = "created_by", nullable = false)
    private String createdBy;    

    @Column(name = "date_created", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;    

    @Column(name = "date_modified")
    @UpdateTimestamp
    private LocalDateTime modifiedDateTime;
}