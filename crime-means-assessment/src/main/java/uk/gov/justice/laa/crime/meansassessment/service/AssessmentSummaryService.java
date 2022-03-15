package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.courtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.courtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.courtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.ReviewType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.WorkType;

import java.util.ArrayList;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentSummaryService {

    private final CourtDataService courtDataService;

    public ApiCreateMeansAssessmentResponse getAssessmentsSummary(final ApiCreateMeansAssessmentResponse assessmentResponse,
                                                                  final String laaTransactionId) {
        var assesmentsSummary = new ArrayList<ApiAssessmentSummary>();

        assesmentsSummary.add(getFinancialAssessmentSummary(assessmentResponse));
        assesmentsSummary.add(getPassportAssessmentSummary(assessmentResponse.getRepId(), laaTransactionId));
        assesmentsSummary.add(getHardshipReviewsSummary(assessmentResponse.getRepId(), laaTransactionId));
        assesmentsSummary.add(getIOJAppealSummary(assessmentResponse.getRepId(), laaTransactionId));

        assessmentResponse.setAssessmentSummary(assesmentsSummary);
        return assessmentResponse;
    }

    private ApiAssessmentSummary getFinancialAssessmentSummary(final ApiCreateMeansAssessmentResponse assessmentResponse) {

        ApiAssessmentSummary finAssessmentSummary = new ApiAssessmentSummary()
                .withId(assessmentResponse.getAssessmentId())
                .withReviewType(ofNullable(assessmentResponse.getReviewType()).map(ReviewType::getCode).orElse(null));

        if (assessmentResponse.getAssessmentType() == AssessmentType.INIT) {
            finAssessmentSummary.setType(WorkType.Initial_Assessment);
            finAssessmentSummary.setStatus(ofNullable(assessmentResponse.getFassInitStatus()).map(CurrentStatus::getDescription).orElse(null));
            finAssessmentSummary.setResult(assessmentResponse.getInitResult());
        } else {
            finAssessmentSummary.setType(WorkType.Full_Means_Test);
            finAssessmentSummary.setStatus(ofNullable(assessmentResponse.getFassFullStatus()).map(CurrentStatus::getDescription).orElse(null));
            finAssessmentSummary.setResult(assessmentResponse.getFullResult());
        }

        if (assessmentResponse.getAssessmentType() == AssessmentType.FULL && assessmentResponse.getFullAssessmentDate() != null) {
            finAssessmentSummary.setAssessmentDate(assessmentResponse.getFullAssessmentDate());
        } else {
            finAssessmentSummary.setAssessmentDate(assessmentResponse.getInitialAssessmentDate());
        }
        return finAssessmentSummary;
    }

    private ApiAssessmentSummary getPassportAssessmentSummary(final Integer repId,
                                                              final String laaTransactionId) {
        PassportAssessmentDTO passportAssessmentDTO = courtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);

        ApiAssessmentSummary passportAssessmentSummary = new ApiAssessmentSummary()
                .withId(passportAssessmentDTO.getId().toString())
                .withType(WorkType.Passported)
                .withAssessmentDate(passportAssessmentDTO.getAssessmentDate())
                .withStatus(CurrentStatus.getFrom(passportAssessmentDTO.getPastStatus()).getDescription())
                .withReviewType(passportAssessmentDTO.getRtCode())
                .withResult(passportAssessmentDTO.getResult());

        return passportAssessmentSummary;
    }

    private ApiAssessmentSummary getHardshipReviewsSummary(final Integer repId,
                                                           final String laaTransactionId) {
        HardshipReviewDTO hardshipReviewDTO = courtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);

        ApiAssessmentSummary hardshipReviewSummary = new ApiAssessmentSummary()
                .withId(hardshipReviewDTO.getId().toString())
                .withAssessmentDate(hardshipReviewDTO.getReviewDate())
                .withStatus(hardshipReviewDTO.getStatus().getDescription())
                .withResult(hardshipReviewDTO.getReviewResult());

        ofNullable(hardshipReviewDTO.getCourtType()).ifPresentOrElse(courtType -> {
            if (courtType.equals("MAGISTRATE")) {
                hardshipReviewSummary.setType(WorkType.Hardship_Review_Magistrate);
            }
        }, () -> hardshipReviewSummary.setType(WorkType.Hardship_Review_CrownCourt));

        return hardshipReviewSummary;
    }

    private ApiAssessmentSummary getIOJAppealSummary(final Integer repId,
                                                     final String laaTransactionId) {
        IOJAppealDTO iojAppealDTO = courtDataService.getIOJAppealFromRepId(repId, laaTransactionId);

        ApiAssessmentSummary IOJAppealSummary = new ApiAssessmentSummary()
                .withId(iojAppealDTO.getId().toString())
                .withAssessmentDate(iojAppealDTO.getAppealSetupDate())
                .withType(WorkType.IoJ_Appeal)
                .withResult(iojAppealDTO.getDecisionResult())
                .withStatus(CurrentStatus.getFrom(iojAppealDTO.getIapsStatus()).getDescription());

        return IOJAppealSummary;
    }

}
