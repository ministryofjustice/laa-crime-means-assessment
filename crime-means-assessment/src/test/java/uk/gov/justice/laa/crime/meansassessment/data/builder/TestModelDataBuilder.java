package uk.gov.justice.laa.crime.meansassessment.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class TestModelDataBuilder {

    // For Assessment Criteria
    public static final Integer TEST_ID = 34;
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
    public static final Integer TEST_DETAIL_ID = 135;
    public static final String TEST_DETAIL_CODE = "TEST_CODE";
    public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    public static final String TEST_SECTION = "SECTION";
    public static final Integer TEST_SEQ = 10;

    public static final Frequency TEST_FREQUENCY = Frequency.MONTHLY;
    public static final CaseType TEST_CASE_TYPE = CaseType.APPEAL_CC;

    public static final BigDecimal TEST_APPLICANT_VALUE = BigDecimal.valueOf(10d);
    public static final BigDecimal TEST_PARTNER_VALUE = BigDecimal.valueOf(1d);

    //create means assessment
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final String MEANS_ASSESSMENT_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    private static final String TEST_SESSION_ID = "TEST_SESSION_ID";

    public static AssessmentCriteriaEntity getAssessmentCriteriaEntityWithChildWeightings(BigDecimal[] weightingFactors) {
        var criteria = getAssessmentCriteriaEntity();
        var weighting1 = getAssessmentCriteriaChildWeightingEntityWithId(37);
        weighting1.setWeightingFactor(weightingFactors[0]);
        var weighting2 = getAssessmentCriteriaChildWeightingEntityWithId(38);
        weighting2.setWeightingFactor(weightingFactors[1]);
        criteria.setAssessmentCriteriaChildWeightings(Set.of(weighting1, weighting2));
        return criteria;
    }

    public static AssessmentCriteriaEntity getAssessmentCriteriaEntityWithDetails() {
        var criteria = getAssessmentCriteriaEntity();
        criteria.setAssessmentCriteriaDetails(
                Set.of(getAssessmentCriteriaDetailEntityWithId())
        );
        return criteria;
    }

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

    public static AssessmentCriteriaChildWeightingEntity getAssessmentCriteriaChildWeightingEntityWithId(int id) {
        var childWeighting = getAssessmentCriteriaChildWeightingEntity();
        childWeighting.setId(id);
        return childWeighting;
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

    public static AssessmentCriteriaDetailEntity getAssessmentCriteriaDetailEntityWithId() {
        var criteriaDetail = getAssessmentCriteriaDetailEntity();
        criteriaDetail.setId(TEST_DETAIL_ID);
        return criteriaDetail;
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
                .withSectionSummaries(getAllApiAssessmentSectionSummaries())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withCaseType(CaseType.EITHER_WAY)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withSectionSummaries(getAllApiAssessmentSectionSummaries());
    }

    public static ApiAssessmentSectionSummary getApiAssessmentSectionSummary() {
        return new ApiAssessmentSectionSummary()
                .withSection("INITA")
                .withAssessmentDetails(
                        List.of(
                                new ApiAssessmentDetail()
                                        .withCriteriaDetailId(132)
                                        .withApplicantAmount(TEST_APPLICANT_VALUE)
                                        .withApplicantFrequency(TEST_FREQUENCY)
                        )
                );
    }

    public static List<ApiAssessmentSectionSummary> getAssessmentSummaries() {
        ApiAssessmentSectionSummary section = getApiAssessmentSectionSummary();
        return List.of(section, new ApiAssessmentSectionSummary()
                .withSection("INITB")
                .withAssessmentDetails(
                        List.of(
                                new ApiAssessmentDetail()
                                        .withCriteriaDetailId(142)
                                        .withApplicantAmount(TEST_APPLICANT_VALUE)
                                        .withApplicantFrequency(TEST_FREQUENCY)
                        )
                )
        );
    }

    public static List<ApiAssessmentSectionSummary> getAllApiAssessmentSectionSummaries() {
        return List.of(
                new ApiAssessmentSectionSummary()
                        .withSection("INITA")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(132)
                                                .withApplicantAmount(TEST_APPLICANT_VALUE)
                                                .withApplicantFrequency(TEST_FREQUENCY)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(133)
                                                .withApplicantAmount(BigDecimal.ZERO)
                                        ,
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(134)
                                                .withApplicantAmount(BigDecimal.ZERO)
                                                .withPartnerAmount(TEST_PARTNER_VALUE)
                                                .withPartnerFrequency(TEST_FREQUENCY)
                                )
                        )
                ,
                new ApiAssessmentSectionSummary()
                        .withSection("INITB")
                        .withAssessmentDetails(
                                Stream.of(
                                        List.of(new ApiAssessmentDetail()
                                                .withCriteriaDetailId(135)
                                                .withApplicantAmount(TEST_APPLICANT_VALUE)
                                                .withApplicantFrequency(TEST_FREQUENCY)
                                                .withPartnerAmount(TEST_PARTNER_VALUE)
                                                .withPartnerFrequency(TEST_FREQUENCY)),

                                        IntStream.range(136, 146).boxed().collect(Collectors.toList())
                                                .stream().map(num -> new ApiAssessmentDetail()
                                                        .withCriteriaDetailId(num)
                                                        .withApplicantAmount(TEST_APPLICANT_VALUE)
                                                        .withApplicantFrequency(TEST_FREQUENCY)
                                                ).collect(Collectors.toList())
                                ).flatMap(Collection::stream).collect(Collectors.toList())
                        )
        );
    }

    public static ApiUserSession getUserSession() {
        return new ApiUserSession()
                .withSessionId("6c45ebfe-fe3a-5f2f-8dad-f7c8f03b8327")
                .withUserName("test-userid");
    }

    public static List<ApiAssessmentChildWeighting> getAssessmentChildWeightings() {
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

    public static ApiNewWorkReason getApiNewWorkReason() {
        return new ApiNewWorkReason()
                .withCode("PBI");
    }

    public static ApiSupplierInfo getApiSupplierInfo() {
        return new ApiSupplierInfo()
                .withAccountNumber("91919")
                .withName("testSupplierName")
                .withAddress(getApiAddress());
    }

    public static ApiAddress getApiAddress() {
        return new ApiAddress()
                .withAddressId(79387182)
                .withLine1("210 Kybald Street")
                .withPostCode("LE7 8OU");
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

    public static ApiAssessmentStatus getApiAssessmentStatus() {
        return new ApiAssessmentStatus()
                .withStatus("testStatus");
    }

    public static AuthorizationResponseDTO getAuthorizationResponseDTO(boolean valid){
        return AuthorizationResponseDTO.builder().result(valid).build();
    }

    public static OutstandingAssessmentResultDTO getOutstandingAssessmentResultDTO(boolean outstandingAssessmentsFound){
        return OutstandingAssessmentResultDTO.builder().outstandingAssessments(outstandingAssessmentsFound).build();
    }


    private static ApiAssessmentStatus getApiAssessmentStatus() {
        var assessmentStatus = new ApiAssessmentStatus();
        assessmentStatus.setStatus("testStatus");
        return assessmentStatus;
    }

    public static List<ApiAssessmentSectionSummary> getApiAssessmentSummaries(boolean isValid) {
        return List.of(new ApiAssessmentSectionSummary()
                .withApplicantAnnualTotal(BigDecimal.valueOf(14))
                .withAnnualTotal(isValid ? BigDecimal.valueOf(15) : null)
                .withAssessmentDetails(getApiAssessmentDetails())
        );
    }

    public static List<ApiAssessmentDetail> getApiAssessmentDetails(boolean withPartner) {
        var assessmentDetail = new ApiAssessmentDetail()
                .withCriteriaDetailId(TEST_DETAIL_ID)
                .withApplicantAmount(TEST_APPLICANT_VALUE)
                .withApplicantFrequency(TEST_FREQUENCY);

        if (withPartner) {
            assessmentDetail
                    .withPartnerAmount(TEST_PARTNER_VALUE)
                    .withPartnerFrequency(TEST_FREQUENCY);
        }
        return List.of(assessmentDetail);
    }

    public static List<ApiAssessmentDetail> getApiAssessmentDetails() {
        return getApiAssessmentDetails(false);
    }
}