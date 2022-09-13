package uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class Assessment {
    protected Integer id;
    protected String newWorkReason;
    protected LocalDateTime dateCreated;
}
