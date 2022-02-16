package uk.gov.justice.laa.crime.meansassessment.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestModelDataBuilder {

    // For Assessment Criteria
    public static final BigDecimal TEST_INITIAL_LOWER_THRESHOLD = BigDecimal.valueOf(12500d);
    public static final BigDecimal TEST_INITIAL_UPPER_THRESHOLD = BigDecimal.valueOf(22500d);
    public static final BigDecimal TEST_FULL_THRESHOLD = BigDecimal.valueOf(5000d);
    public static final BigDecimal TEST_PARTNER_WEIGHTING_FACTOR = BigDecimal.valueOf(0.3d);
    public static final String TEST_USER = "TEST";
    public static final BigDecimal TEST_LIVING_ALLOWANCE = BigDecimal.valueOf(10000d);
    public static final BigDecimal TEST_ELIGIBILITY_THRESHOLD = BigDecimal.valueOf(35000d);
    public static final BigDecimal TEST_APPLICANT_WEIGHTING_FACTOR = BigDecimal.ONE;
    public static final LocalDateTime TEST_DATE_FROM = LocalDateTime.now().minusDays(1);
    public static final LocalDateTime TEST_DATE_TO = LocalDateTime.now().plusDays(2);

    // Assessment Criteria Child Weighting
    public static final Integer TEST_INITIAL_LOWER_AGE_RANGE = 3;
    public static final Integer TEST_INITIAL_UPPER_AGE_RANGE = 5;
    public static final BigDecimal TEST_WEIGHTING_FACTOR = BigDecimal.ONE;

    // Assessment Criteria Details
    public static final String TEST_DETAIL_CODE = "TEST_CODE";
    public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    public static final String TEST_SECTION = "SECTION";
    public static final Integer TEST_SEQ = 10;

    public static final Frequency TEST_FREQUENCY = Frequency.MONTHLY;
    public static final CaseType TEST_CASE_TYPE = CaseType.APPEAL_CC;

    private static final BigDecimal TEST_APPLICANT_VALUE = BigDecimal.valueOf(10d);
    private static final BigDecimal TEST_PARTNER_VALUE = BigDecimal.valueOf(1d);

    //create means assessment
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final String MEANS_ASSESSMENT_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";

    public static AssessmentCriteriaEntity getAssessmentCriteriaEntity() {
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
                .assessmentCriteria(AssessmentCriteriaEntity.builder().id(1).build())
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
                .caseType(TEST_CASE_TYPE)
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
        return new ApiCreateMeansAssessmentRequest()
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withRepId(isValid ? 91919 : null)
                .withCmuId(isValid ? 91919 : null)
                .withUserId("test-userid")
                .withTransactionDateTime(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withNewWorkReason(getApiNewWorkReason())
                .withSupplierInfo(getApiSupplierInfo())
                .withSectionSummaries(getAssessmentSummaries())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withSectionSummaries(getAssessmentSectionSummaries());
    }

    private static List<ApiAssessmentSectionSummary> getAssessmentSectionSummaries() {
        return List.of(
                new ApiAssessmentSectionSummary()
                        .withSection("INITA")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(132)
                                                .withApplicantAmount(BigDecimal.valueOf(500))
                                                .withApplicantFrequency(new ApiFrequency().withCode("MONTHLY"))
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(133)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(134)
                                                .withPartnerAmount(BigDecimal.valueOf(250))
                                                .withPartnerFrequency(new ApiFrequency().withCode("MONTHLY"))
                                )
                        )
                ,
                new ApiAssessmentSectionSummary()
                        .withSection("INITB")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(135)
                                                .withApplicantAmount(BigDecimal.valueOf(200))
                                                .withApplicantFrequency(new ApiFrequency().withCode("WEEKLY"))
                                                .withPartnerAmount(BigDecimal.valueOf(5000))
                                                .withPartnerFrequency(new ApiFrequency().withCode("ANNUALLY"))
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(136)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(137)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(138)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(139)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(140)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(141)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(142)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(143)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(144)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(145)
                                )
                        )
        );
    }

    private static ApiUserSession getUserSession() {
        return new ApiUserSession()
                .withSessionId("6c45ebfe-fe3a-5f2f-8dad-f7c8f03b8327")
                .withUserName("test-userid");
    }

    private static List<ApiAssessmentChildWeighting> getAssessmentChildWeightings() {
        return List.of(
                new ApiAssessmentChildWeighting()
                        .withWeightingId(37)
                        .withWeightingFactor(BigDecimal.valueOf(0.15))
                        .withLowerAgeRange(0)
                        .withUpperAgeRange(1)
                        .withNoOfChildren(1)
                ,
                new ApiAssessmentChildWeighting()
                        .withWeightingId(38)
                        .withWeightingFactor(BigDecimal.valueOf(0.3))
                        .withLowerAgeRange(2)
                        .withUpperAgeRange(4)
                        .withNoOfChildren(2)
        );
    }

    private static ApiNewWorkReason getApiNewWorkReason() {
        return new ApiNewWorkReason()
                .withCode("PBI");
    }

    private static ApiSupplierInfo getApiSupplierInfo() {
        return new ApiSupplierInfo()
                .withAccountNumber("91919")
                .withName("testSupplierName")
                .withAddress(getApiAddress());
    }

    private static ApiAddress getApiAddress() {
        return new ApiAddress()
                .withAddressId(79387182)
                .withLine1("210 Kybald Street")
                .withPostCode("LE7 8OU");
    }

    private static List<ApiAssessmentSectionSummary> getAssessmentSummaries() {
        return List.of(new ApiAssessmentSectionSummary()
                .withApplicantAnnualTotal(BigDecimal.valueOf(10.00))
                .withAnnualTotal(BigDecimal.valueOf(10.00))
                .withAssessmentDetails(getAssessmentDetails())
        );
    }

    public static ApiCreateMeansAssessmentResponse getCreateMeansAssessmentResponse(boolean isValid) {
        return new ApiCreateMeansAssessmentResponse()
                .withAssessmentId("7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327")
                .withCriteriaId(isValid ? 41 : null)
                .withTotalAggregatedIncome(BigDecimal.valueOf(10))
                .withAdjustedIncomeValue(BigDecimal.valueOf(11))
                .withAdjustedIncomeValue(BigDecimal.valueOf(12))
                .withLowerThreshold(BigDecimal.valueOf(12))
                .withUpperThreshold(BigDecimal.valueOf(13))
                .withResult("testResult")
                .withResultReason("testResultReason")
                .withAssessmentStatus(isValid ? getApiAssessmentStatus() : null)
                .withAssessmentSummary(getApiAssessmentSummaries(isValid));
    }

    private static ApiAssessmentStatus getApiAssessmentStatus() {
        return new ApiAssessmentStatus()
                .withStatus("testStatus");
    }

    private static List<ApiAssessmentSectionSummary> getApiAssessmentSummaries(boolean isValid) {
        return List.of(new ApiAssessmentSectionSummary()
                .withApplicantAnnualTotal(BigDecimal.valueOf(14))
                .withAnnualTotal(isValid ? BigDecimal.valueOf(15) : null)
                .withAssessmentDetails(getAssessmentDetails())
        );
    }

    private static List<ApiAssessmentDetail> getAssessmentDetails() {
        return List.of(new ApiAssessmentDetail()
                .withCriteriaDetailId(1000)
                .withApplicantAmount(BigDecimal.valueOf(16))
                .withApplicantFrequency(getFrequency())
        );
    }

    private static ApiFrequency getFrequency() {
        return new ApiFrequency()
                .withCode("test-code");
    }
}