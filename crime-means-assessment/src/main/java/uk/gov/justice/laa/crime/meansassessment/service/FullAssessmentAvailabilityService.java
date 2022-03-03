package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitialAssessmentResult;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.MagCourtOutcome;

import java.util.Objects;

@Slf4j
@Service
public class FullAssessmentAvailabilityService {

    public void processFullAssessmentAvailable(final ApiCreateMeansAssessmentRequest meansAssessmentRequest,
                                               final ApiCreateMeansAssessmentResponse meansAssessmentResponse) {

        log.info("Start full assessment available check");

        Objects.requireNonNull(meansAssessmentRequest, "meansAssessmentRequest must not be null");
        Objects.requireNonNull(meansAssessmentResponse, "meansAssessmentResponse must not be null");

        if (!Objects.isNull(meansAssessmentRequest.getAssessmentDate())) {
            meansAssessmentResponse.setFullAssessmentAvailable(true);
        } else {
            switch (InitialAssessmentResult.valueOf(meansAssessmentResponse.getResult())) {
                case FULL: {
                    meansAssessmentResponse.setFullAssessmentAvailable(true);
                    break;
                }
                case FAIL: {
                    processFullAssessmentAvailableOnFail(meansAssessmentRequest, meansAssessmentResponse);
                    break;
                }
                case HARDSHIP: {
                    if (meansAssessmentRequest.getNewWorkReason().getCode().equals("HR"))
                        meansAssessmentResponse.setFullAssessmentAvailable(true);
                    break;
                }
                default:
                    meansAssessmentResponse.setFullAssessmentAvailable(false);
            }
        }
    }

    private void processFullAssessmentAvailableOnFail(final ApiCreateMeansAssessmentRequest meansAssessmentRequest,
                                                      final ApiCreateMeansAssessmentResponse meansAssessmentResponse) {
        switch (meansAssessmentRequest.getCaseType()) {
            case INDICTABLE:
            case CC_ALREADY:
            case APPEAL_CC: {
                meansAssessmentResponse.setFullAssessmentAvailable(true);
                break;
            }
            case EITHER_WAY: {
                if (meansAssessmentRequest.getMagCourtOutcome().equals(MagCourtOutcome.COMMITTED_FOR_TRIAL))
                    meansAssessmentResponse.setFullAssessmentAvailable(true);
            }
        }
    }

}


