--liquibase formatted sql
--changeset lucast:01-create-defendant-assessment-table
CREATE TABLE IF NOT EXISTS mla.defendant_assessment
(
    "defendant_assessment_id" text NOT NULL,
    "updated_info" text,
    "created_date_time" timestamp with time zone NOT NULL,
    "updated_date_time" timestamp with time zone,
    PRIMARY KEY ("defendant_assessment_id")
)
    WITH (
        OIDS = FALSE
    );

ALTER TABLE IF EXISTS mla.defendant_assessment
    OWNER to postgres;