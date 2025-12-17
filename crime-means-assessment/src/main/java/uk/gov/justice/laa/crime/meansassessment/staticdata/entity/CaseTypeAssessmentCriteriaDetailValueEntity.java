package uk.gov.justice.laa.crime.meansassessment.staticdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.Frequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(
        name = "case_type_ass_detail_values",
        schema = "crime_means_assessment",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_ctadv_catycasetype_acrdid",
                    columnNames = {"caty_case_type", "acrd_id"})
        })
public class CaseTypeAssessmentCriteriaDetailValueEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "caty_case_type")
    private CaseType caseType;

    @ManyToOne
    @JoinColumn(name = "acrd_id", nullable = false)
    private AssessmentCriteriaDetailEntity assessmentCriteriaDetail;

    @Column(name = "applicant_value")
    private BigDecimal applicantValue;

    @Column(name = "partner_value")
    private BigDecimal partnerValue;

    @Column(name = "applicant_freq_code")
    private Frequency applicantFrequency;

    @Column(name = "partner_freq_code")
    private Frequency partnerFrequency;

    @Column(name = "date_created", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDateTime;

    @Column(name = "date_modified")
    @UpdateTimestamp
    private LocalDateTime modifiedDateTime;
}
