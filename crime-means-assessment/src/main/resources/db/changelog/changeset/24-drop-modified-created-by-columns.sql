--liquibase formatted sql
--changeset dedwards:24-drop-modified-created-by-columns.sql

ALTER TABLE crime_means_assessment.assessment_details
    DROP COLUMN IF EXISTS created_by;
ALTER TABLE crime_means_assessment.assessment_details
    DROP COLUMN IF EXISTS modified_by;

ALTER TABLE crime_means_assessment.ass_criteria_details
    DROP COLUMN IF EXISTS created_by;
ALTER TABLE crime_means_assessment.ass_criteria_details
    DROP COLUMN IF EXISTS modified_by;

ALTER TABLE crime_means_assessment.ass_criteria_detail_freq
    DROP COLUMN IF EXISTS created_by;
ALTER TABLE crime_means_assessment.ass_criteria_detail_freq
    DROP COLUMN IF EXISTS modified_by;

ALTER TABLE crime_means_assessment.case_type_ass_detail_values
    DROP COLUMN IF EXISTS created_by;
ALTER TABLE crime_means_assessment.case_type_ass_detail_values
    DROP COLUMN IF EXISTS modified_by;


