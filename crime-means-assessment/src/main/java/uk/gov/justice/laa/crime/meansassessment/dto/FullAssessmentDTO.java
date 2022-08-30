package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullAssessmentDTO {

    @Builder.Default
    private  List<AssessmentSectionSummaryDTO> assessmentSectionSummaries = new ArrayList<>();
}
