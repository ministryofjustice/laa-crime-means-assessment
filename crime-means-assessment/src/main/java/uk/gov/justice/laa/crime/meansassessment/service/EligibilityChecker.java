package uk.gov.justice.laa.crime.meansassessment.service;

import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;

public interface EligibilityChecker {
    boolean isEligibilityCheckRequired(MeansAssessmentRequestDTO requestDTO);
}
