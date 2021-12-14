--liquibase formatted sql
--changeset rajeshk:08-assessment-details-table-create
CREATE TABLE IF NOT EXISTS crime_means_assessment.assessment_details
(
    detail_code VARCHAR(10),
    description VARCHAR(100),
    date_created TIMESTAMP DEFAULT current_timestamp NOT NULL,
    created_by VARCHAR(100) DEFAULT current_user NOT NULL,
    date_modified TIMESTAMP,
    modified_by VARCHAR(100),
    CONSTRAINT pk_assessment_details PRIMARY KEY (detail_code)
);