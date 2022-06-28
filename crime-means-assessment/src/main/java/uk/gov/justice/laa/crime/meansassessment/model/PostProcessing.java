package uk.gov.justice.laa.crime.meansassessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;



@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PostProcessing {
    private Integer repId;
    private String laaTransactionId;
}
