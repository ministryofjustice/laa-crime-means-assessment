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

    public ApiCreateMeansAssessmentResponse addAssessmentSummaryToMeansResponse(final ApiCreateMeansAssessmentResponse assessmentResponse,
                                                                                final String laaTransactionId) {
        log.info("Generating assessment summary for means assessment response");
        try {
            var assesmentsSummary = new ArrayList<ApiAssessmentSummary>();

            getFinancialAssessmentSummary(assessmentResponse).ifPresent(assesmentsSummary::add);
            getPassportAssessmentSummary(assessmentResponse.getRepId(), laaTransactionId).ifPresent(assesmentsSummary::add);
            getHardshipReviewsSummary(assessmentResponse.getRepId(), laaTransactionId).ifPresent(assesmentsSummary::add);
            getIOJAppealSummary(assessmentResponse.getRepId(), laaTransactionId).ifPresent(assesmentsSummary::add);

            assessmentResponse.setAssessmentSummary(assesmentsSummary);
        } catch (Exception ex) {
            log.error("Failed to generate assessment summary for means assessment response", ex);
        }
        return assessmentResponse;
    }

    private Optional<ApiAssessmentSummary> getFinancialAssessmentSummary(final ApiCreateMeansAssessmentResponse assessmentResponse) {

        log.info("Generating assessment summary for financial assessment");
        ApiAssessmentSummary finAssessmentSummary = new ApiAssessmentSummary();
        try {
            finAssessmentSummary.withId(assessmentResponse.getAssessmentId())
                    .withReviewType(ofNullable(assessmentResponse.getReviewType()).map(ReviewType::getCode).orElse(null));

            if (assessmentResponse.getAssessmentType() == AssessmentType.INIT) {
                finAssessmentSummary.setType(WorkType.Initial_Assessment);
                finAssessmentSummary.setStatus(ofNullable(assessmentResponse.getFassInitStatus()).map(CurrentStatus::getDescription)
                        .orElse(null));
                finAssessmentSummary.setResult(assessmentResponse.getInitResult());
            } else {
                finAssessmentSummary.setType(WorkType.Full_Means_Test);
                finAssessmentSummary.setStatus(ofNullable(assessmentResponse.getFassFullStatus()).map(CurrentStatus::getDescription)
                        .orElse(null));
                finAssessmentSummary.setResult(assessmentResponse.getFullResult());
            }

            if (assessmentResponse.getAssessmentType() == AssessmentType.FULL && assessmentResponse.getFullAssessmentDate() != null) {
                finAssessmentSummary.setAssessmentDate(assessmentResponse.getFullAssessmentDate());
            } else {
                finAssessmentSummary.setAssessmentDate(assessmentResponse.getInitialAssessmentDate());
            }
            return of(finAssessmentSummary);
        } catch (Exception ex) {
            log.error("Error generating financial assessment summary", ex);
            return empty();
        }
    }

    private Optional<ApiAssessmentSummary> getPassportAssessmentSummary(final Integer repId, final String laaTransactionId) {
        log.info("Generating assessment summary for passport assessment");
        try {
            ApiAssessmentSummary passportAssessmentSummary = new ApiAssessmentSummary();
            PassportAssessmentDTO passportAssessmentDTO = maatCourtDataService.getPassportAssessmentFromRepId(repId, laaTransactionId);
            passportAssessmentSummary.withId(passportAssessmentDTO.getId().toString())
                    .withType(WorkType.Passported)
                    .withAssessmentDate(passportAssessmentDTO.getAssessmentDate())
                    .withStatus(CurrentStatus.getFrom(passportAssessmentDTO.getPastStatus()).getDescription())
                    .withReviewType(passportAssessmentDTO.getRtCode())
                    .withResult(passportAssessmentDTO.getResult());
            return of(passportAssessmentSummary);
        } catch (Exception ex) {
            log.error("Failed to retrieve passportAssessmentDTO from court-data-api", ex);
            return empty();
        }
    }

    private Optional<ApiAssessmentSummary> getHardshipReviewsSummary(final Integer repId, final String laaTransactionId) {
        log.info("Generating assessment summary for hardship reviews");
        try {
            ApiAssessmentSummary hardshipReviewSummary = new ApiAssessmentSummary();
            HardshipReviewDTO hardshipReviewDTO = maatCourtDataService.getHardshipReviewFromRepId(repId, laaTransactionId);

            hardshipReviewSummary.withId(hardshipReviewDTO.getId().toString())
                    .withAssessmentDate(hardshipReviewDTO.getReviewDate())
                    .withStatus(hardshipReviewDTO.getStatus().getDescription())
                    .withResult(hardshipReviewDTO.getReviewResult());

            ofNullable(hardshipReviewDTO.getCourtType()).ifPresentOrElse(courtType -> {
                if (courtType.equals("MAGISTRATE")) {
                    hardshipReviewSummary.setType(WorkType.Hardship_Review_Magistrate);
                }
            }, () -> hardshipReviewSummary.setType(WorkType.Hardship_Review_CrownCourt));
            return of(hardshipReviewSummary);
        } catch (Exception ex) {
            log.error("Failed to retrieve hardshipReviewDTO from court-data-api", ex);
            return empty();
        }

    }

    private Optional<ApiAssessmentSummary> getIOJAppealSummary(final Integer repId, final String laaTransactionId) {
        log.info("Generating assessment summary for IOJ Appeal");
        try {
            ApiAssessmentSummary iOJAppealSummary = new ApiAssessmentSummary();
            IOJAppealDTO iojAppealDTO = maatCourtDataService.getIOJAppealFromRepId(repId, laaTransactionId);

            iOJAppealSummary.withId(iojAppealDTO.getId().toString())
                    .withAssessmentDate(iojAppealDTO.getAppealSetupDate())
                    .withType(WorkType.IoJ_Appeal)
                    .withResult(iojAppealDTO.getDecisionResult())
                    .withStatus(CurrentStatus.getFrom(iojAppealDTO.getIapsStatus()).getDescription());
            return of(iOJAppealSummary);
        } catch (Exception ex) {
            log.error("Failed to retrieve iojAppealDTO from court-data-api", ex);
            return empty();
        }
    }

}
