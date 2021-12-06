--liquibase formatted sql
--changeset lucast:02-create-defendant-assessment-table
CREATE TABLE IF NOT EXISTS crime_means_assessment.defendant_assessment
(
    defendant_assessment_id VARCHAR NOT NULL,
    updated_info VARCHAR,
    created_date_time timestamp with time zone NOT NULL,
    updated_date_time timestamp with time zone,
    PRIMARY KEY (defendant_assessment_id)
);