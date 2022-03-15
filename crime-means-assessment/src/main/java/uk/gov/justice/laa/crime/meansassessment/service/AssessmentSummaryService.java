package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.dto.courtdata.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.WorkType;

import java.util.ArrayList;

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

        //get hardship reviews summary
        //get IOJ appeals summary

        assessmentResponse.setAssessmentSummary(assesmentsSummary);
        return assessmentResponse;
    }

    private ApiAssessmentSummary getFinancialAssessmentSummary(final ApiCreateMeansAssessmentResponse assessmentResponse) {

        ApiAssessmentSummary finAssessmentSummary = new ApiAssessmentSummary()
                .withId(assessmentResponse.getAssessmentId())
                .withReviewType(assessmentResponse.getReviewType().getCode());

        if (assessmentResponse.getAssessmentType() == AssessmentType.INIT) {
            finAssessmentSummary.setType(WorkType.Initial_Assessment);
            finAssessmentSummary.setStatus(assessmentResponse.getFassInitStatus().getDescription());
            finAssessmentSummary.setResult(assessmentResponse.getInitResult());
        } else {
            finAssessmentSummary.setType(WorkType.Full_Means_Test);
            finAssessmentSummary.setStatus(assessmentResponse.getFassFullStatus().getDescription());
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
                .withStatus(passportAssessmentDTO.getPastStatus())
                .withReviewType(passportAssessmentDTO.getRtCode())
                .withResult(passportAssessmentDTO.getResult());

        return passportAssessmentSummary;
    }

}
