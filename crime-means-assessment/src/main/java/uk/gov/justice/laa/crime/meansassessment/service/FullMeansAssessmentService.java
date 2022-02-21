package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class FullMeansAssessmentService {

    public void createFullAssessment(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) {

        log.info("Starting full means assessment");

        //something

        log.info("Finished full means assessment");
    }
}