--liquibase formatted sql
--changeset muthus:20-income-evidence-table-create

CREATE TABLE IF NOT EXISTS crime_means_assessment.income_evidence
(
    EVIDENCE VARCHAR(20),
    DESCRIPTION VARCHAR(100),
    DATE_CREATED TIMESTAMP,
    USER_CREATED VARCHAR(100),
    DATE_MODIFIED TIMESTAMP,
    USER_MODIFIED VARCHAR(100),
    LETTER_DESCRIPTION VARCHAR(500),
    WELSH_LETTER_DESCRIPTION VARCHAR(500),
    ADHOC VARCHAR(1),
    CONSTRAINT pk_income_evidence PRIMARY KEY (EVIDENCE)
    );