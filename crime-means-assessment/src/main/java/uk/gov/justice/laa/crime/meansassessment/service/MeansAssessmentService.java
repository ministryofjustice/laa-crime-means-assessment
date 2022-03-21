package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.validation.InitialAssessmentValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final InitialMeansAssessmentService initialMeansAssessmentService;
    private final FullMeansAssessmentService fullMeansAssessmentService;
    private final InitialAssessmentValidator initialAssessmentValidator;

    public ApiCreateMeansAssessmentResponse createAssessment(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) throws ValidationException {
        log.info("Starting - Create means assessment");

        if (apiCreateMeansAssessmentRequest.getAssessmentType() == AssessmentType.INIT) {

          initialAssessmentValidator.validate(apiCreateMeansAssessmentRequest);
            initialMeansAssessmentService.createInitialAssessment(apiCreateMeansAssessmentRequest);

        } else {

            fullMeansAssessmentService.createFullAssessment(apiCreateMeansAssessmentRequest);

        }
        log.info("Finished - Create means assessment");
        return null;
    }
}
