package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.validation.InitialAssessmentValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final InitialMeansAssessmentService initialMeansAssessmentService;
    private final FullMeansAssessmentService fullMeansAssessmentService;
    private final InitialAssessmentValidator initialAssessmentValidator;
    private final AssessmentSummaryService assessmentSummaryService;

    public ApiCreateMeansAssessmentResponse createAssessment(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) throws ValidationException {
        log.info("Starting - Create means assessment");
        ApiCreateMeansAssessmentResponse createMeansAssessmentResponse;

        if (apiCreateMeansAssessmentRequest.getAssessmentType() == AssessmentType.INIT) {
            initialAssessmentValidator.validate(apiCreateMeansAssessmentRequest);
            createMeansAssessmentResponse = initialMeansAssessmentService.createInitialAssessment(apiCreateMeansAssessmentRequest);
        } else {
            createMeansAssessmentResponse = fullMeansAssessmentService.createFullAssessment(apiCreateMeansAssessmentRequest);
        }

        //post processing
        assessmentSummaryService.addAssessmentSummaryToMeansResponse(createMeansAssessmentResponse, apiCreateMeansAssessmentRequest.getLaaTransactionId());

        log.info("Finished - Create means assessment");
        return createMeansAssessmentResponse;
    }
}
