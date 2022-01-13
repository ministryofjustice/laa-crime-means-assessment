--liquibase formatted sql
--changeset lucast:16-defendant-assessment-table-drop.sql
DROP TABLE IF EXISTS crime_means_assessment.defendant_assessment;