--liquibase formatted sql
--changeset rajeshk:10-ass-criteria-details-table-create
CREATE TABLE IF NOT EXISTS crime_means_assessment.ass_criteria_details
(
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    ass_criteria_id BIGINT NOT NULL,
    asde_detail_code VARCHAR(10) NOT NULL,
    section VARCHAR(10) NOT NULL,
    seq INT NOT NULL,
    description VARCHAR(50) NOT NULL,
    use_frequency BOOLEAN,
    date_created TIMESTAMP NOT NULL,
    created_by VARCHAR(10) NOT NULL,
    date_modified TIMESTAMP,
    modified_by VARCHAR(10),
    CONSTRAINT pk_assessment_criteria_detail PRIMARY KEY (id),
    CONSTRAINT uk_acdt_seq_section UNIQUE (ass_criteria_id, seq, section),
    CONSTRAINT fk_acdt_asde FOREIGN KEY (asde_detail_code)
    REFERENCES crime_means_assessment.assessment_details (detail_code),
    CONSTRAINT fk_acdt_ascr FOREIGN KEY (ass_criteria_id)
    REFERENCES crime_means_assessment.assessment_criteria (id)
);