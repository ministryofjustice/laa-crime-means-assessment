package uk.gov.justice.laa.crime.meansassessment.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationResponse {

    //change to caseWorkerStatus
    private boolean result;
}
