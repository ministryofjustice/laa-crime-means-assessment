package uk.gov.justice.laa.crime.meansassessment.initial.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentStatus;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSummary;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiFrequency;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.initial.ApiCreateMeansAssessmentResponse;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class InitialMeansAssessmentService {

    public ApiCreateMeansAssessmentResponse createAssessment(ApiCreateMeansAssessmentRequest meansAssessment){
        log.info("Create initial means assessment - Start");
        var response = getCreateMeansAssessmentResponse();

        return response;
    }

    private static ApiCreateMeansAssessmentResponse getCreateMeansAssessmentResponse() {
        var meansAssessmentResponse = ApiCreateMeansAssessmentResponse.builder()
                .assessmentId("7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327")
                .criteriaId(41)
                .totalAggregatedIncome(Double.valueOf("10.00"))
                .adjustedIncomeValue(Double.valueOf("11.00"))
                .lowerThreshold(Double.valueOf("12.00"))
                .upperThreshold(Double.valueOf("13.00"))
                .result("testResult")
                .resultReason("testResultReason")
                .assessmentStatus(getApiAssessmentStatus())
                .assessmentSummary(getApiAssessmentSummaries())
                .build();
        return meansAssessmentResponse;
    }
    private static ApiAssessmentStatus getApiAssessmentStatus(){
        var assessmentStatus = ApiAssessmentStatus.builder()
                .status("testStatus")
                .build();
        return assessmentStatus;
    }
    private static List<ApiAssessmentSummary> getApiAssessmentSummaries(){
        var assessmentSummary = ApiAssessmentSummary.builder()
                .applicantAnnualTotal(Double.valueOf("14.00"))
                .annualTotal(Double.valueOf("15.00"))
                .build();
        return List.of(assessmentSummary);
    }
    private static List<ApiAssessmentDetail> getAssessmentDetails(){
        var assessmentDetail = ApiAssessmentDetail.builder()
                .criteriaDetailsId("7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327")
                .applicantAmount(Double.valueOf("16.00"))
                .applicantFrequency(getFrequency())
                .build();

        return List.of(assessmentDetail);
    }
    private static ApiFrequency getFrequency(){
        var frequency = ApiFrequency.builder()
                .code("test-code")
                .build();
        return frequency;
    }
}
