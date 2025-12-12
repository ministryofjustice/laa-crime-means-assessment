package uk.gov.justice.laa.crime.meansassessment.staticdata.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "assessment_details", schema = "crime_means_assessment")
public class AssessmentDetailEntity {
    @Id
    @Column(name = "DETAIL_CODE", nullable = false)
    private String detailCode;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(name = "DATE_MODIFIED")
    @UpdateTimestamp
    private LocalDateTime modifiedDateTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assessmentDetail")
    @ToString.Exclude
    private Set<AssessmentCriteriaDetailEntity> assessmentCriteriaDetails;
}
