package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitialAssessmentResult;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.MagCourtOutcome.COMMITTED_FOR_TRIAL;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason.HR;

@Slf4j
@Service
public class FullAssessmentAvailabilityService {

    public void processFullAssessmentAvailable(final ApiCreateMeansAssessmentRequest meansAssessmentRequest,
                                               final ApiCreateMeansAssessmentResponse meansAssessmentResponse) {

        log.info("Start full assessment available check for create means assessment request {} and response {}",
                meansAssessmentRequest, meansAssessmentResponse);

        requireNonNull(meansAssessmentRequest, "meansAssessmentRequest must not be null");
        requireNonNull(meansAssessmentResponse, "meansAssessmentResponse must not be null");

        meansAssessmentResponse.setFullAssessmentAvailable(false);

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
                    ofNullable(meansAssessmentRequest.getNewWorkReason()).ifPresent(newWorkReason -> {
                        if (newWorkReason == HR) {
                            meansAssessmentResponse.setFullAssessmentAvailable(true);
                        } else {
                            meansAssessmentResponse.setFullAssessmentAvailable(false);
                        }
                    });
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
                ofNullable(meansAssessmentRequest.getMagCourtOutcome()).ifPresent(magCourtOutcome -> {
                    if (magCourtOutcome == COMMITTED_FOR_TRIAL) {
                        meansAssessmentResponse.setFullAssessmentAvailable(true);
                    } else {
                        meansAssessmentResponse.setFullAssessmentAvailable(false);
                    }
                });
                break;
            }
            default:
                meansAssessmentResponse.setFullAssessmentAvailable(false);
        }
    }

}


