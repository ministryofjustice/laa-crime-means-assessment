--liquibase formatted sql
--changeset rajeshk:12-ass-criteria-detail-freq-table-create
CREATE TABLE IF NOT EXISTS crime_means_assessment.ass_criteria_detail_freq
(
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    acrd_id BIGINT NOT NULL,
    freq_code VARCHAR(8) NOT NULL,
    date_created TIMESTAMP NOT NULL,
    created_by VARCHAR(10) NOT NULL,
    date_modified TIMESTAMP,
    modified_by VARCHAR(10),
    CONSTRAINT pk_ass_criteria_detail_freq PRIMARY KEY (id),
    CONSTRAINT uk_acdf_acrdid_freqcode UNIQUE (acrd_id, freq_code),
    CONSTRAINT fk_acdf_acrd FOREIGN KEY (acrd_id)
    REFERENCES crime_means_assessment.ass_criteria_details (id)
);