--liquibase formatted sql
--changeset lucast:02-defendant-assessment-table-test-data-insert
INSERT INTO mla.defendant_assessment ("defendant_assessment_id", "updated_info", "created_date_time", "updated_date_time") VALUES ('484cf7b4-b910-4f28-82bd-b60c69467053', 'Test updated info', '2021-11-23 10:49:09.201845+00', '2021-11-23 10:49:09.201845+00');