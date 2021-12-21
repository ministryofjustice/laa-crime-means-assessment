package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final AssessmentCriteriaRepository assessmentCriteriaRepository;

    protected List<AssessmentCriteriaEntity> getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) throws AssessmentCriteriaNotFoundException {
        List<AssessmentCriteriaEntity> assessmentCriteriaForDate = assessmentCriteriaRepository.findAssessmentCriteriaForDate(assessmentDate);
        if(!assessmentCriteriaForDate.isEmpty()){
            // If there is no partner or there is a partner with contrary interest, set partnerWeightingFactor to null
            if(!hasPartner ||  contraryInterest){
                assessmentCriteriaForDate.forEach(ac -> ac.setPartnerWeightingFactor(null));
            }
            return assessmentCriteriaForDate;
        } else {
            log.error("No Assessment Criteria found for date {}", assessmentDate);
            throw new AssessmentCriteriaNotFoundException(String.format("No Assessment Criteria found for date %s",assessmentDate));
        }
    }

    public ApiCreateMeansAssessmentResponse createInitialAssessment(ApiCreateMeansAssessmentRequest meansAssessment){
        log.info("Create initial means assessment - Start");
        var response = TemporaryCreateMeansAssessmentReponse.getCreateMeansAssessmentResponse();

        return response;
    }

    private static class TemporaryCreateMeansAssessmentReponse{
        private static ApiCreateMeansAssessmentResponse getCreateMeansAssessmentResponse() {
            var mar = new ApiCreateMeansAssessmentResponse();
            mar.setAssessmentId("7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327");
            mar.setCriteriaId(41);
            mar.setTotalAggregatedIncome(Double.valueOf("10.00"));
            mar.setAdjustedIncomeValue(Double.valueOf("11.00"));
            mar.setLowerThreshold(Double.valueOf("12.00"));
            mar.setUpperThreshold(Double.valueOf("13.00"));
            mar.setResult("testResult");
            mar.setResultReason("testResultReason");
            mar.setAssessmentStatus(getApiAssessmentStatus());
            mar.setAssessmentSummary(getApiAssessmentSummaries());

            return mar;
        }
        private static ApiAssessmentStatus getApiAssessmentStatus(){
            var assessmentStatus = new ApiAssessmentStatus();
            assessmentStatus.setStatus("testStatus");
            return assessmentStatus;
        }
        private static List<ApiAssessmentSummary> getApiAssessmentSummaries(){
            var assessmentSummary = new ApiAssessmentSummary();
            assessmentSummary.setApplicantAnnualTotal(Double.valueOf("14.00"));
            assessmentSummary.setAnnualTotal(Double.valueOf("15.00"));
            assessmentSummary.setAssessmentDetail(getAssessmentDetails());
            return List.of(assessmentSummary);
        }
        private static List<ApiAssessmentDetail> getAssessmentDetails(){
            var assessmentDetail = new ApiAssessmentDetail();
            assessmentDetail.setCriteriaDetailsId("7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327");
            assessmentDetail.setApplicantAmount(Double.valueOf("16.00"));
            assessmentDetail.setApplicantFrequency(getFrequency());
            return List.of(assessmentDetail);
        }
        private static ApiFrequency getFrequency(){
            var frequency = new ApiFrequency();
            frequency.setCode("test-code");
            return frequency;
        }
    }
}
