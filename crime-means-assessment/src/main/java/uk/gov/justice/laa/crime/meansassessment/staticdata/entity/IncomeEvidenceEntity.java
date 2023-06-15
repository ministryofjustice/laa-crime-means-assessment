package uk.gov.justice.laa.crime.meansassessment.staticdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "income_evidence", schema = "crime_means_assessment" )
public class IncomeEvidenceEntity {
    @Id
    @Column(name = "EVIDENCE", nullable = false, length = 20)
    private String id;

    @Column(name = "DESCRIPTION", nullable = false, length = 100)
    private String description;

    @Column(name = "LETTER_DESCRIPTION", length = 500)
    private String letterDescription;

    @Column(name = "WELSH_LETTER_DESCRIPTION", length = 500)
    private String welshLetterDescription;

    @Column(name = "ADHOC", length = 1)
    private String adhoc;

}