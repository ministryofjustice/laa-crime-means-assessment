package uk.gov.justice.laa.crime.meansassessment.defendant.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "DEFENDANT_ASSESSMENT", schema = "CRIME_MEANS_ASSESSMENT")
public class DefendantAssessmentEntity {
    @Id
    @Column(name = "DEFENDANT_ASSESSMENT_ID")
    private String id;
    @Column(name = "UPDATED_INFO")
    @EqualsAndHashCode.Exclude private String updatedInfo;
    @Column(name = "CREATED_DATE_TIME",nullable = false, updatable = false)
    @CreationTimestamp
    @EqualsAndHashCode.Exclude private LocalDateTime createdDateTime;
    @Column(name = "UPDATED_DATE_TIME")
    @UpdateTimestamp
    @EqualsAndHashCode.Exclude private LocalDateTime updatedDateTime;
}
