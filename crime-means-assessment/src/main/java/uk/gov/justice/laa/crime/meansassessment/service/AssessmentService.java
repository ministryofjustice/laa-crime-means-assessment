package uk.gov.justice.laa.crime.meansassessment.service;

import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;

public interface AssessmentService {
    MeansAssessmentDTO execute(BigDecimal summariesTotal, ApiCreateMeansAssessmentRequest meansAssessment, AssessmentCriteriaEntity assessmentCriteria);
}
