package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.HardshipReviewDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.IOJAppealDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.ReviewType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.WorkType;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Optional.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentSummaryService {

    private final MaatCourtDataService maatCourtDataService;

    public void addAssessmentSummaryToMeansResponse(final ApiCreateMeansAssessmentResponse assessmentResponse,
                                                    final String laaTransactionId) {
        log.info("Generating assessment summary for means assessment response");
        try {
            var assessmentsSummary = new ArrayList<ApiAssessmentSummary>();

            assessmentsSummary.add(getFinancialAssessmentSummary(assessmentResponse));
            getPassportAssessmentSummary(assessmentResponse.getRepId(), laaTransactionId).ifPresent(assessmentsSummary::add);
            getHardshipReviewsSummary(assessmentResponse.getRepId(), laaTransactionId).ifPresent(assessmentsSummary::add);
            getIOJAppealSummary(assessmentResponse.getRepId(), laaTransactionId).ifPresent(assessmentsSummary::add);
            assessmentResponse.setAssessmentSummary(assessmentsSummary);
        } catch (Exception ex) {
            log.error("Failed to generate assessment summary for means assessment response with assessmentId: {}", assessmentResponse.getAssessmentId(), ex);
        }
    }

    private ApiAssessmentSummary getFinancialAssessmentSummary(final ApiCreateMeansAssessmentResponse assessmentResponse) {
        log.info("Generating assessment summary for financial assessment for repId: {}", assessmentResponse.getRepId());
        try {
            ApiAssessmentSummary finAssessmentSummary = new ApiAssessmentSummary();
            finAssessmentSummary.withId(assessmentResponse.getAssessmentId())
                    .withReviewType(ofNullable(assessmentResponse.getReviewType()).map(ReviewType::getCode).orElse(null));

            if (assessmentResponse.getAssessmentType() == AssessmentType.INIT) {
                finAssessmentSummary.setType(WorkType.INITIAL_ASSESSMENT);
                finAssessmentSummary.setStatus(ofNullable(assessmentResponse.getFassInitStatus()).map(CurrentStatus::getDescription)
                        .orElse(null));
                finAssessmentSummary.setResult(assessmentResponse.getInitResult());
            } else {
                finAssessmentSummary.setType(WorkType.FULL_MEANS_TEST);
                finAssessmentSummary.setStatus(ofNullable(assessmentResponse.getFassFullStatus()).map(CurrentStatus::getDescription)
                        .orElse(null));
                finAssessmentSummary.setResult(assessmentResponse.getFullResult());
            }
            if (assessmentResponse.getAssessmentType() == AssessmentType.FULL && assessmentResponse.getFullAssessmentDate() != null) {
                finAssessmentSummary.setAssessmentDate(assessmentResponse.getFullAssessmentDate());
            } else {
                finAssessmentSummary.setAssessmentDate(assessmentResponse.getInitialAssessmentDate());
            }
            log.info("Returning finAssessmentSummary for : {}", finAssessmentSummary);
            return finAssessmentSummary;
        } catch (Exception ex) {
            log.error("Error generating financial assessment summary for repId: {}", assessmentResponse.getRepId(), ex);
            throw ex;
        }
    }

    private Optional<ApiAssessmentSummary> getPassportAssessmentSummary(final Integer repId, final String laaTransactionId) {
        log.info("Generating assessment summary for passport assessment for repId: {}", repId);
        try {
            PassportAssessmentDTO passportAssessmentDTO = maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);

            if (passportAssessmentDTO != null) {
                ApiAssessmentSummary passportAssessmentSummary = new ApiAssessmentSummary();
                passportAssessmentSummary.withId(passportAssessmentDTO.getId())
                        .withType(WorkType.PASSPORTED)
                        .withAssessmentDate(passportAssessmentDTO.getAssessmentDate())
                        .withStatus(CurrentStatus.getFrom(passportAssessmentDTO.getPastStatus()).getDescription())
                        .withReviewType(passportAssessmentDTO.getRtCode())
                        .withResult(passportAssessmentDTO.getResult());
                return of(passportAssessmentSummary);
            }
            return empty();
        } catch (Exception ex) {
            log.error("Error generating passport assessment summary for repId: {}", repId, ex);
            throw ex;
        }
    }

    private Optional<ApiAssessmentSummary> getHardshipReviewsSummary(final Integer repId, final String laaTransactionId) {
        log.info("Generating assessment summary for hardship reviews for repId: {}", repId);
        try {
            HardshipReviewDTO hardshipReviewDTO = maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);

            if (hardshipReviewDTO != null) {
                ApiAssessmentSummary hardshipReviewSummary = new ApiAssessmentSummary();
                hardshipReviewSummary.withId(hardshipReviewDTO.getId())
                        .withAssessmentDate(hardshipReviewDTO.getReviewDate())
                        .withStatus(hardshipReviewDTO.getStatus().getDescription())
                        .withResult(hardshipReviewDTO.getReviewResult());

                ofNullable(hardshipReviewDTO.getCourtType()).ifPresentOrElse(courtType -> {
                    if (courtType.equals("MAGISTRATE")) {
                        hardshipReviewSummary.setType(WorkType.HARDSHIP_REVIEW_MAGISTRATE);
                    }
                }, () -> hardshipReviewSummary.setType(WorkType.HARDSHIP_REVIEW_CROWN_COURT));
                return of(hardshipReviewSummary);
            }
            return empty();
        } catch (Exception ex) {
            log.error("Error generating hardship review summary for repId: {}", repId, ex);
            throw ex;
        }
    }

    private Optional<ApiAssessmentSummary> getIOJAppealSummary(final Integer repId, final String laaTransactionId) {
        log.info("Generating assessment summary for IOJ Appeal for repId: {}", repId);
        try {
            IOJAppealDTO iojAppealDTO = maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);

            if (iojAppealDTO != null) {
                ApiAssessmentSummary iOJAppealSummary = new ApiAssessmentSummary();
                iOJAppealSummary.withId(iojAppealDTO.getId())
                        .withAssessmentDate(iojAppealDTO.getAppealSetupDate())
                        .withType(WorkType.IOJ_APPEAL)
                        .withResult(iojAppealDTO.getDecisionResult())
                        .withStatus(CurrentStatus.getFrom(iojAppealDTO.getIapsStatus()).getDescription());
                return of(iOJAppealSummary);
            }
            return empty();
        } catch (Exception ex) {
            log.error("Error generating iojAppeal summary for repId: {}", repId, ex);
            throw ex;
        }
    }
}
