package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.util.Objects;

import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.MagCourtOutcome.COMMITTED_FOR_TRIAL;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason.HR;

@Slf4j
@Service
public class FullAssessmentAvailabilityService {

    public void processFullAssessmentAvailable(final ApiCreateMeansAssessmentRequest meansAssessmentRequest,
                                               final ApiCreateMeansAssessmentResponse meansAssessmentResponse) {

        log.info("Start full assessment available check for create means assessment request {} and response {}",
                meansAssessmentRequest, meansAssessmentResponse);

        meansAssessmentResponse.setFullAssessmentAvailable(false);

        if (!Objects.isNull(meansAssessmentRequest.getFullAssessmentDate())) {
            meansAssessmentResponse.setFullAssessmentAvailable(true);
        } else {
            switch (InitAssessmentResult.getFrom(meansAssessmentResponse.getInitResult())) {
                case FULL: {
                    meansAssessmentResponse.setFullAssessmentAvailable(true);
                    break;
                }
                case FAIL: {
                    processFullAssessmentAvailableOnResultFail(meansAssessmentRequest, meansAssessmentResponse);
                    break;
                }
                case HARDSHIP: {
                    if (meansAssessmentRequest.getNewWorkReason() == HR) {
                        meansAssessmentResponse.setFullAssessmentAvailable(true);
                    }
                }
            }
        }
        log.info("fullAssessmentAvailable set to {}", meansAssessmentResponse.getFullAssessmentAvailable());
    }

    private void processFullAssessmentAvailableOnResultFail(final ApiCreateMeansAssessmentRequest meansAssessmentRequest,
                                                            final ApiCreateMeansAssessmentResponse meansAssessmentResponse) {
        switch (meansAssessmentRequest.getCaseType()) {
            case INDICTABLE:
            case CC_ALREADY:
            case APPEAL_CC: {
                meansAssessmentResponse.setFullAssessmentAvailable(true);
                break;
            }
            case EITHER_WAY: {
                if (meansAssessmentRequest.getMagCourtOutcome() == COMMITTED_FOR_TRIAL) {
                    meansAssessmentResponse.setFullAssessmentAvailable(true);
                }
            }
        }
    }
}


