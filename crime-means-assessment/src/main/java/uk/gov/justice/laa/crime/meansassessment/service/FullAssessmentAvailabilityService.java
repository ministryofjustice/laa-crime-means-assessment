package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.InitAssessmentResult;

import java.util.Objects;

import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.MagCourtOutcome.COMMITTED_FOR_TRIAL;
import static uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason.HR;

@Slf4j
@Service
public class FullAssessmentAvailabilityService {

    public void processFullAssessmentAvailable(final MeansAssessmentRequestDTO requestDTO,
                                               final ApiCreateMeansAssessmentResponse meansAssessmentResponse) {

        log.debug("Start full assessment available check for create means assessment request {} and response {}",
                requestDTO, meansAssessmentResponse);

        meansAssessmentResponse.setFullAssessmentAvailable(false);

        if (!Objects.isNull(meansAssessmentResponse.getFullAssessmentDate())) {
            meansAssessmentResponse.setFullAssessmentAvailable(true);
        } else {
            InitAssessmentResult initAssessmentResult = InitAssessmentResult.getFrom(meansAssessmentResponse.getInitResult());
            if (initAssessmentResult != null) {
                switch (initAssessmentResult) {
                    case PASS:
                        break;
                    case FULL:
                        meansAssessmentResponse.setFullAssessmentAvailable(true);
                        break;
                    case FAIL:
                        processFullAssessmentAvailableOnResultFail(requestDTO, meansAssessmentResponse);
                        break;
                    case HARDSHIP:
                        checkNewWorkReason(requestDTO, meansAssessmentResponse);
                }
            }
        }
        log.info("fullAssessmentAvailable set to {}", meansAssessmentResponse.getFullAssessmentAvailable());
    }

    private void checkNewWorkReason(MeansAssessmentRequestDTO requestDTO, ApiCreateMeansAssessmentResponse meansAssessmentResponse) {
        if (requestDTO.getNewWorkReason() == HR) {
            meansAssessmentResponse.setFullAssessmentAvailable(true);
        }
    }


    private void processFullAssessmentAvailableOnResultFail(final MeansAssessmentRequestDTO requestDTO,
                                                            final ApiCreateMeansAssessmentResponse meansAssessmentResponse) {
        switch (requestDTO.getCaseType()) {
            case COMMITAL:
            case SUMMARY_ONLY:
            case INDICTABLE:
            case CC_ALREADY:
            case APPEAL_CC:
                meansAssessmentResponse.setFullAssessmentAvailable(true);
                break;
            case EITHER_WAY:
                checkMagCourtOutcome(requestDTO, meansAssessmentResponse);
        }
    }

    private void checkMagCourtOutcome(MeansAssessmentRequestDTO requestDTO, ApiCreateMeansAssessmentResponse meansAssessmentResponse) {
        if (requestDTO.getMagCourtOutcome() == COMMITTED_FOR_TRIAL) {
            meansAssessmentResponse.setFullAssessmentAvailable(true);
        }
    }
}


