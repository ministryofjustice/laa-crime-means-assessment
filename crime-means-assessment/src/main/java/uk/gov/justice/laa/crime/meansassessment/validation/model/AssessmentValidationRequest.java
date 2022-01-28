package uk.gov.justice.laa.crime.meansassessment.validation.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentValidationRequest {

    private int repId;
    private String userName;
    private String userAction;
    private String assessmentType;
}
