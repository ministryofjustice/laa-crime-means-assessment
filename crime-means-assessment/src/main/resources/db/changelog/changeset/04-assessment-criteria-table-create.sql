--liquibase formatted sql
--changeset rajeshk:04-assessment-criteria-table-create
CREATE TABLE IF NOT EXISTS crime_means_assessment.assessment_criteria
(
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
	date_from TIMESTAMP NOT NULL,
    date_to TIMESTAMP,
	initial_lower_threshold DECIMAL(12,2) NOT NULL,
	initial_upper_threshold DECIMAL(12,2) NOT NULL,
	full_threshold DECIMAL(12,2) NOT NULL,
	applicant_weighting_factor DECIMAL(4,2) NOT NULL,
    partner_weighting_factor DECIMAL(4,2) NOT NULL,
    living_allowance DECIMAL(12,2),
    eligibility_threshold DECIMAL(12,2),
	date_created TIMESTAMP NOT NULL,
	created_by VARCHAR(10) NOT NULL,
	date_modified TIMESTAMP,
	modified_by VARCHAR(10),
    CONSTRAINT pk_assessment_criteria PRIMARY KEY (id)
);