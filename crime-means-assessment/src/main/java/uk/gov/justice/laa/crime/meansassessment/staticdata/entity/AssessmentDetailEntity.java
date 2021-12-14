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

@Data @Builder @AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "assessment_details", schema = "crime_means_assessment")
public class AssessmentDetailEntity {
    @Id
    @Column(name = "DETAIL_CODE", nullable = false)
    private String detailCode;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

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
}