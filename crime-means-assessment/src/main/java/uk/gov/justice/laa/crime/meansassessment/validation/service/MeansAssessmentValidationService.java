package uk.gov.justice.laa.crime.meansassessment.validation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.validation.CreateAssessmentValidator;
import uk.gov.justice.laa.crime.meansassessment.validation.model.AssessmentValidationRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentValidationService {

    private final CreateAssessmentValidator createAssessmentValidator;

    public void validate(ApiCreateMeansAssessmentRequest meansAssessment) {

        AssessmentValidationRequest assessmentValidationRequest = getValidationRequest(meansAssessment);

        createAssessmentValidator.validate(assessmentValidationRequest);

    }

    private AssessmentValidationRequest getValidationRequest(ApiCreateMeansAssessmentRequest meansAssessment) {
        return AssessmentValidationRequest.builder()
                .repId(meansAssessment.getRepId())
                .userName(meansAssessment.getUserId())
                .userAction("CREATE_ASSESSMENT")
                .build();
    }

}
