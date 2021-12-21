package uk.gov.justice.laa.crime.meansassessment.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestModelDataBuilder {
    public static final String DEFENDANT_ASSESSMENT_ID = "484cf7b4-b910-4f28-82bd-b60c69467053";
    public static final String DEFENDANT_ASSESSMENT_UPDATED_INFO = "updated info test";

    // For Assessment Criteria
    public static final BigDecimal TEST_INITIAL_LOWER_THRESHOLD =  BigDecimal.valueOf(12500d);
    public static final BigDecimal TEST_INITIAL_UPPER_THRESHOLD =  BigDecimal.valueOf(22500d);
    public static final BigDecimal TEST_FULL_THRESHOLD = BigDecimal.valueOf(5000d);
    public static final BigDecimal TEST_PARTNER_WEIGHTING_FACTOR = BigDecimal.valueOf(0.3d);
    public static final String TEST_USER = "TEST";
    public static final BigDecimal TEST_LIVING_ALLOWANCE =  BigDecimal.valueOf(10000d);
    public static final BigDecimal TEST_ELIGIBILITY_THRESHOLD =  BigDecimal.valueOf(35000d);
    public static final BigDecimal TEST_APPLICANT_WEIGHTING_FACTOR = BigDecimal.ONE;
    public static final LocalDateTime TEST_DATE_FROM = LocalDateTime.now().minusDays(1);
    public static final LocalDateTime TEST_DATE_TO = LocalDateTime.now().plusDays(2);
    public static final Long TEST_ASSESSMENT_CRITERIA_ID = 99999l;

    // Assessment Criteria Child Weighting
    public static final BigDecimal TEST_INITIAL_LOWER_AGE_RANGE =  BigDecimal.valueOf(3d);
    public static final BigDecimal TEST_INITIAL_UPPER_AGE_RANGE =  BigDecimal.valueOf(5d);
    public static final BigDecimal TEST_WEIGHTING_FACTOR = BigDecimal.ONE;

    // Assessment Criteria Details
    public static final String TEST_DETAIL_CODE = "TEST_CODE";
    public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    public static final String TEST_SECTION = "SECTION";
    public static final Integer TEST_SEQ = 10;

    public static final Frequency TEST_FREQUENCY = Frequency.MONTHLY;
    public static final CaseType TEST_CASETYPE = CaseType.APPEAL_CC;

    private static final BigDecimal TEST_APPLICANT_VALUE = BigDecimal.valueOf(10d);
    private static final BigDecimal TEST_PARTNER_VALUE = BigDecimal.valueOf(1d);

    //create means assessment
     public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final String MEANS_ASSESSMENT_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";

    public static DefendantAssessmentEntity getDefendantAssessmentDTO(){
        return DefendantAssessmentEntity.builder()
                .id(DEFENDANT_ASSESSMENT_ID)
                .updatedInfo(DEFENDANT_ASSESSMENT_UPDATED_INFO)
                .build();
    }

     public static AssessmentCriteriaEntity getAssessmentCriteriaEntity(){
         return AssessmentCriteriaEntity.builder()
                 .dateFrom(TEST_DATE_FROM)
                 .dateTo(TEST_DATE_TO)
                 .initialLowerThreshold(TEST_INITIAL_LOWER_THRESHOLD)
                 .initialUpperThreshold(TEST_INITIAL_UPPER_THRESHOLD)
                 .fullThreshold(TEST_FULL_THRESHOLD)
                 .applicantWeightingFactor(TEST_APPLICANT_WEIGHTING_FACTOR)
                 .partnerWeightingFactor(TEST_PARTNER_WEIGHTING_FACTOR)
                 .createdDateTime(LocalDateTime.now())
                 .createdBy(TEST_USER)
                 .modifiedDateTime(LocalDateTime.now())
                 .modifiedBy(TEST_USER)
                 .livingAllowance(TEST_LIVING_ALLOWANCE)
                 .eligibilityThreshold(TEST_ELIGIBILITY_THRESHOLD)
                 .build();
     }

    public static AssessmentCriteriaChildWeightingEntity getAssessmentCriteriaChildWeightingEntity() {
        return AssessmentCriteriaChildWeightingEntity.builder()
                .assessmentCriteria(AssessmentCriteriaEntity.builder().id(1L).build())
                .lowerAgeRange(TEST_INITIAL_LOWER_AGE_RANGE)
                .upperAgeRange(TEST_INITIAL_UPPER_AGE_RANGE)
                .weightingFactor(TEST_WEIGHTING_FACTOR)
                .userCreated(TEST_USER)
                .build();
    }

    public static AssessmentDetailEntity getAssessmentDetailEntity() {
        return AssessmentDetailEntity.builder()
                .detailCode(TEST_DETAIL_CODE)
                .description(TEST_DESCRIPTION)
                .createdDateTime(LocalDateTime.now())
                .createdBy(TEST_USER)
                .modifiedDateTime(LocalDateTime.now())
                .modifiedBy(TEST_USER)
                .build();
    }

    public static AssessmentCriteriaDetailEntity getAssessmentCriteriaDetailEntity() {
        return AssessmentCriteriaDetailEntity.builder()
                .description(TEST_DESCRIPTION)
                .section(TEST_SECTION)
                .seq(TEST_SEQ)
                .createdDateTime(LocalDateTime.now())
                .createdBy(TEST_USER)
                .modifiedDateTime(LocalDateTime.now())
                .modifiedBy(TEST_USER)
                .build();
    }

    public static AssessmentCriteriaDetailFrequencyEntity getAssessmentCriteriaDetailFrequencyEntity() {
        return AssessmentCriteriaDetailFrequencyEntity.builder()
                .frequency(TEST_FREQUENCY)
                .createdDateTime(LocalDateTime.now())
                .createdBy(TEST_USER)
                .modifiedDateTime(LocalDateTime.now())
                .modifiedBy(TEST_USER)
                .build();
    }

    public static CaseTypeAssessmentCriteriaDetailValueEntity getCaseTypeAssessmentCriteriaDetailValueEntity() {
        return CaseTypeAssessmentCriteriaDetailValueEntity.builder()
                .applicantValue(TEST_APPLICANT_VALUE)
                .caseType(TEST_CASETYPE)
                .applicantFrequency(TEST_FREQUENCY)
                .partnerFrequency(TEST_FREQUENCY)
                .partnerValue(TEST_PARTNER_VALUE)
                .createdDateTime(LocalDateTime.now())
                .createdBy(TEST_USER)
                .modifiedDateTime(LocalDateTime.now())
                .modifiedBy(TEST_USER)
                .build();
    }


    public static ApiCreateMeansAssessmentRequest getCreateMeansAssessmentRequest(boolean isValid) {
        var mas = new ApiCreateMeansAssessmentRequest();
        mas.setLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID);
        mas.setRepId(isValid ? 91919 : null);
        mas.setCmuId(isValid ? 91919 : null);
        mas.setUserId("test-userid");
        mas.setTransactionDateTime(LocalDateTime.of(2021,12,16,10,0));
        mas.setAssessmentDate(LocalDateTime.of(2021,12,16,10,0));
        mas.setNewWorkReason(getApiNewWorkReason());
        mas.setSupplierInfo(getApiSupplierInfo());
        mas.setAssessmentSummary(getAssessmentSummaries());
        mas.setHasPartner(true);
        mas.setPartnerContraryInterest(false);

        return mas;
    }
    private static ApiNewWorkReason getApiNewWorkReason(){
        var apiNewWorkReason = new ApiNewWorkReason();
        apiNewWorkReason.setCode("PBI");
        return apiNewWorkReason;
    }
    private static ApiSupplierInfo getApiSupplierInfo(){
        var apiSupplierInfo = new ApiSupplierInfo();

        apiSupplierInfo.setAccountNumber(91919);
        apiSupplierInfo.setName("testSupplierName");
        apiSupplierInfo.setAddress(getApiAddress());

        return apiSupplierInfo;
    }

    private static ApiAddress getApiAddress(){
        var apiAddress = new ApiAddress();

        apiAddress.setAddressId("79387182");
        apiAddress.setLine1("210 Kybald Street");
        apiAddress.setPostCode("LE7 8OU");

        return apiAddress;
    }

    private static List<ApiAssessmentSummary> getAssessmentSummaries(){
        var apiAssessmentSummary = new ApiAssessmentSummary();
        apiAssessmentSummary.setApplicantAnnualTotal(Double.valueOf("10.00"));
        apiAssessmentSummary.setAnnualTotal(Double.valueOf("10.00"));
        apiAssessmentSummary.setAssessmentDetail(getAssessmentDetails());
        return List.of(apiAssessmentSummary);
    }

    public static ApiCreateMeansAssessmentResponse getCreateMeansAssessmentResponse(boolean isValid) {
        var mar = new ApiCreateMeansAssessmentResponse();
        mar.setAssessmentId("7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327");
        mar.setCriteriaId(isValid ? 41 : null);
        mar.setTotalAggregatedIncome(Double.valueOf("10.00"));
        mar.setAdjustedIncomeValue(Double.valueOf("11.00"));
        mar.setLowerThreshold(Double.valueOf("12.00"));
        mar.setUpperThreshold(Double.valueOf("13.00"));
        mar.setResult("testResult");
        mar.setResultReason("testResultReason");
        mar.setAssessmentStatus(isValid ? getApiAssessmentStatus() : null);
        mar.setAssessmentSummary(getApiAssessmentSummaries(isValid));

        return mar;
    }
    private static ApiAssessmentStatus getApiAssessmentStatus(){
        var assessmentStatus = new ApiAssessmentStatus();
        assessmentStatus.setStatus("testStatus");
        return assessmentStatus;
    }
    private static List<ApiAssessmentSummary> getApiAssessmentSummaries(boolean isValid){
        var assessmentSummary = new ApiAssessmentSummary();
        assessmentSummary.setApplicantAnnualTotal(Double.valueOf("14.00"));
        assessmentSummary.setAnnualTotal(isValid ? Double.valueOf("15.00") : null);

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