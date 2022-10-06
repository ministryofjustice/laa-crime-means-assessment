package uk.gov.justice.laa.crime.meansassessment.data.builder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.*;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.*;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class TestModelDataBuilder {

    // For Assessment Criteria
    public static final Integer TEST_CRITERIA_ID = 34;
    public static final BigDecimal TEST_INITIAL_LOWER_THRESHOLD = BigDecimal.valueOf(12500d);
    public static final BigDecimal TEST_INITIAL_UPPER_THRESHOLD = BigDecimal.valueOf(22500d);
    public static final BigDecimal TEST_FULL_THRESHOLD = BigDecimal.valueOf(5000d);

    public static final BigDecimal TEST_APPLICANT_ANNUAL_TOTAL = BigDecimal.valueOf(120d);
    public static final BigDecimal TEST_APPLICANT_WEIGHTING_FACTOR = BigDecimal.valueOf(0.1);
    public static final BigDecimal TEST_PARTNER_WEIGHTING_FACTOR = BigDecimal.valueOf(0.3d);
    public static final BigDecimal TEST_TOTAL_CHILD_WEIGHTING = BigDecimal.valueOf(0.2);

    public static final String TEST_USER = "TEST";
    public static final BigDecimal TEST_LIVING_ALLOWANCE = BigDecimal.valueOf(10000d);
    public static final BigDecimal TEST_ELIGIBILITY_THRESHOLD = BigDecimal.valueOf(35000d);
    public static final LocalDateTime TEST_DATE_FROM = LocalDateTime.now().minusDays(1);
    public static final LocalDateTime TEST_DATE_TO = LocalDateTime.now().plusDays(2);

    // Assessment Criteria Child Weighting
    public static final Integer TEST_INITIAL_LOWER_AGE_RANGE = 3;
    public static final Integer TEST_INITIAL_UPPER_AGE_RANGE = 5;
    public static final BigDecimal TEST_WEIGHTING_FACTOR = BigDecimal.ONE;

    // Assessment Criteria Details
    public static final Integer TEST_DETAIL_ID = 2;
    public static final Integer TEST_CRITERIA_DETAIL_ID = 135;
    public static final String TEST_DETAIL_CODE = "TEST_CODE";
    public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    public static final String TEST_SECTION = "SECTION";
    public static final Integer TEST_SEQ = 10;

    public static final String TEST_EMPLOYMENT_STATUS = "EMPLOY";
    public static final Integer TEST_USN = 4056595;
    public static final String TEST_NOTE = "TEST_NOTE";
    public static final String TEST_INCOME_NOTE = "TEST_INCOME_NOTE";

    public static final Frequency TEST_FREQUENCY = Frequency.MONTHLY;
    public static final CaseType TEST_CASE_TYPE = CaseType.APPEAL_CC;

    public static final BigDecimal TEST_APPLICANT_VALUE = BigDecimal.valueOf(10d);
    public static final BigDecimal TEST_PARTNER_VALUE = BigDecimal.valueOf(1d);

    public static final CurrentStatus TEST_ASSESSMENT_STATUS = CurrentStatus.COMPLETE;

    public static final LocalDateTime TEST_INCOME_UPLIFT_APPLY_DATE =
            LocalDateTime.of(2021, 12, 12, 0, 0, 0);

    public static final LocalDateTime TEST_INCOME_UPLIFT_REMOVE_DATE =
            TEST_INCOME_UPLIFT_APPLY_DATE.plusDays(10);

    public static final LocalDateTime TEST_INCOME_EVIDENCE_DUE_DATE =
            LocalDateTime.of(2020, 10, 5, 0, 0, 0);

    public static final LocalDate TEST_MAGS_OUTCOME_DATE = LocalDate.of(2022, 6, 5);
    public static final LocalDateTime TEST_DATE_CREATED =
            LocalDateTime.of(2021, 10, 9, 15, 1, 25);

    public static final BigDecimal TEST_DISPOSABLE_INCOME = BigDecimal.valueOf(4000d);
    public static final BigDecimal TEST_ADJUSTED_LIVING_ALLOWANCE = BigDecimal.valueOf(6000d);
    public static final BigDecimal TEST_AGGREGATED_EXPENDITURE = BigDecimal.valueOf(2000d);
    public static final BigDecimal TEST_AGGREGATED_INCOME = BigDecimal.valueOf(12000d);
    public static final BigDecimal TEST_ADJUSTED_INCOME = BigDecimal.valueOf(20000d);

    //create means assessment
    public static final Integer TEST_REP_ID = 42312;
    public static final int MEANS_ASSESSMENT_ID = 1000;
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final Integer TEST_ASSESSMENT_DETAILS_ID = 41681819;
    public static final String TEST_ASSESSMENT_TYPE_INIT = "INIT";
    public static final String TEST_ASSESSMENT_TYPE_FULL = "FULL";
    public static final String TEST_ASSESSMENT_SECTION_INITA = "INITA";
    public static final String TEST_ASSESSMENT_SECTION_INITB = "INITB";
    public static final String TEST_ASSESSMENT_SECTION_FULLA = "FULLA";
    public static final String TEST_ASSESSMENT_SECTION_FULLB = "FULLB";
    public static final int CMU_ID = 30;
    private static final Integer TEST_FINANCIAL_ASSESSMENT_ID = 63423;

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
        criteriaDetail.setId(TEST_CRITERIA_DETAIL_ID);
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

    public static IncomeEvidenceEntity getIncomeEvidenceEntity() {
        return IncomeEvidenceEntity.builder()
                .adhoc("Y")
                .dateCreated(LocalDateTime.now())
                .dateModified(LocalDateTime.now())
                .description("Signature")
                .id("SIGNATURE")
                .userModified(null)
                .userCreated(TEST_USER)
                .letterDescription("Signature")
                .welshLetterDescription("Llofnod")
                .build();
    }

    public static ApiMeansAssessmentRequest getApiMeansAssessmentRequest(boolean isValid) {
        return new ApiMeansAssessmentRequest()
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withRepId(isValid ? 91919 : null)
                .withCmuId(isValid ? 91919 : null)
                .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withOtherBenefitNote(TEST_NOTE)
                .withOtherIncomeNote(TEST_NOTE)
                .withInitAssessmentNotes(TEST_NOTE)
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withCaseType(CaseType.EITHER_WAY)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withEmploymentStatus(TEST_EMPLOYMENT_STATUS)
                .withTimestamp(TEST_DATE_CREATED)
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withMagCourtOutcome(MagCourtOutcome.COMMITTED_FOR_TRIAL)
                .withSectionSummaries(List.of(getApiAssessmentSectionSummary()));
    }

    public static ApiCreateMeansAssessmentRequest getApiCreateMeansAssessmentRequest(boolean isValid) {
        return new ApiCreateMeansAssessmentRequest()
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withReviewType(ReviewType.NAFI)
                .withRepId(isValid ? 91919 : null)
                .withCmuId(isValid ? 91919 : null)
                .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withNewWorkReason(NewWorkReason.PBI)
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withCaseType(CaseType.EITHER_WAY)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withEmploymentStatus(TEST_EMPLOYMENT_STATUS)
                .withUsn(TEST_USN)
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withSectionSummaries(List.of(getApiAssessmentSectionSummary()));
    }

    public static ApiUpdateMeansAssessmentRequest getApiUpdateMeansAssessmentRequest(boolean isValid) {
        return new ApiUpdateMeansAssessmentRequest()
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withRepId(isValid ? 91919 : null)
                .withCmuId(isValid ? 91919 : null)
                .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withFullAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withOtherHousingNote(TEST_NOTE)
                .withInitTotalAggregatedIncome(TEST_AGGREGATED_INCOME)
                .withFullAssessmentNotes(TEST_NOTE)
                .withCaseType(CaseType.EITHER_WAY)
                .withEmploymentStatus(TEST_EMPLOYMENT_STATUS)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withSectionSummaries(List.of(getApiAssessmentSectionSummary()));
    }

    public static MeansAssessmentRequestDTO getMeansAssessmentRequestDTO(boolean isValid) {
        return MeansAssessmentRequestDTO.builder()
                .laaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .repId(isValid ? 91919 : null)
                .cmuId(isValid ? 91919 : null)
                .initialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .assessmentStatus(CurrentStatus.COMPLETE)
                .sectionSummaries(List.of(getApiAssessmentSectionSummary()))
                .childWeightings(getAssessmentChildWeightings())
                .hasPartner(true)
                .partnerContraryInterest(false)
                .assessmentType(AssessmentType.INIT)
                .caseType(CaseType.EITHER_WAY)
                .userSession(getUserSession())
                .incomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .crownCourtOverview(getApiCrownCourtOverview())
                .reviewType(ReviewType.NAFI)
                .magCourtOutcome(MagCourtOutcome.COMMITTED_FOR_TRIAL)
                .newWorkReason(NewWorkReason.PBI)
                .fullAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .financialAssessmentId(TEST_FINANCIAL_ASSESSMENT_ID)
                .build();
    }

    public static ApiCrownCourtOverview getApiCrownCourtOverview() {
        return new ApiCrownCourtOverview()
                .withAvailable(true)
                .withCrownCourtSummary(
                        new ApiCrownCourtSummary()
                                .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                );
    }

    public static ApiIncomeEvidenceSummary getApiIncomeEvidenceSummary() {
        return new ApiIncomeEvidenceSummary()
                .withIncomeEvidenceNotes(TEST_NOTE)
                .withEvidenceDueDate(TEST_INCOME_EVIDENCE_DUE_DATE)
                .withUpliftAppliedDate(TEST_INCOME_UPLIFT_APPLY_DATE)
                .withUpliftRemovedDate(TEST_INCOME_UPLIFT_REMOVE_DATE);
    }

    public static MeansAssessmentDTO getMeansAssessmentDTO() {
        return MeansAssessmentDTO.builder()
                .meansAssessment(getMeansAssessmentRequestDTO(true))
                .assessmentCriteria(getAssessmentCriteriaEntity())
                .totalAggregatedIncome(TEST_AGGREGATED_INCOME)
                .userCreated(TEST_USER)
                .currentStatus(CurrentStatus.COMPLETE)
                .adjustedIncomeValue(TEST_ADJUSTED_INCOME)
                .totalAnnualDisposableIncome(TEST_DISPOSABLE_INCOME)
                .totalAggregatedExpense(TEST_AGGREGATED_EXPENDITURE)
                .adjustedLivingAllowance(TEST_ADJUSTED_LIVING_ALLOWANCE)
                .initAssessmentResult(InitAssessmentResult.FULL)
                .fullAssessmentResult(FullAssessmentResult.PASS)
                .build();
    }

    public static ApiAssessmentSectionSummary getApiAssessmentSectionSummary() {
        return new ApiAssessmentSectionSummary()
                .withApplicantAnnualTotal(TEST_APPLICANT_ANNUAL_TOTAL)
                .withAnnualTotal(TEST_APPLICANT_ANNUAL_TOTAL)
                .withPartnerAnnualTotal(BigDecimal.ZERO)
                .withSection("INITA")
                .withAssessmentDetails(
                        new ArrayList<>(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(TEST_CRITERIA_DETAIL_ID)
                                                .withApplicantAmount(TEST_APPLICANT_VALUE)
                                                .withApplicantFrequency(TEST_FREQUENCY)
                                )
                        )
                );
    }

    public static List<ApiAssessmentSectionSummary> getAssessmentSummaries() {
        ApiAssessmentSectionSummary section = getApiAssessmentSectionSummary();
        return List.of(section, new ApiAssessmentSectionSummary()
                .withSection("INITB")
                .withAssessmentDetails(
                        new ArrayList<>(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(142)
                                                .withApplicantAmount(TEST_APPLICANT_VALUE)
                                                .withApplicantFrequency(TEST_FREQUENCY)
                                )
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
                .withUserName(TEST_USER);
    }

    public static List<ApiAssessmentChildWeighting> getAssessmentChildWeightings() {
        return List.of(
                new ApiAssessmentChildWeighting()
                        .withId(1234)
                        .withChildWeightingId(37)
                        .withNoOfChildren(1)
                ,
                new ApiAssessmentChildWeighting()
                        .withId(2345)
                        .withChildWeightingId(38)
                        .withNoOfChildren(2)
        );
    }

    public static ApiMeansAssessmentResponse getInitMeansAssessmentResponse(boolean isValid) {
        return new ApiMeansAssessmentResponse()
                .withAssessmentId(MEANS_ASSESSMENT_ID)
                .withCriteriaId(isValid ? 41 : null)
                .withTotalAggregatedIncome(BigDecimal.valueOf(10))
                .withAdjustedIncomeValue(BigDecimal.valueOf(11))
                .withAdjustedIncomeValue(BigDecimal.valueOf(12))
                .withLowerThreshold(BigDecimal.valueOf(12))
                .withUpperThreshold(BigDecimal.valueOf(13))
                .withInitResult(InitAssessmentResult.PASS.getResult())
                .withInitResultReason(InitAssessmentResult.PASS.getReason())
                .withFassInitStatus(isValid ? CurrentStatus.COMPLETE : null)
                .withAssessmentSectionSummary(getApiAssessmentSummaries(isValid));
    }

    public static ApiMeansAssessmentResponse getFullMeansAssessmentResponse(boolean isValid) {
        return getInitMeansAssessmentResponse(isValid)
                .withFullResult(FullAssessmentResult.PASS.getResult())
                .withFullResultReason(FullAssessmentResult.PASS.getReason())
                .withFullThreshold(BigDecimal.TEN)
                .withFassFullStatus(isValid ? CurrentStatus.COMPLETE : null)
                .withAdjustedIncomeValue(TEST_ADJUSTED_INCOME)
                .withTotalAnnualDisposableIncome(TEST_DISPOSABLE_INCOME)
                .withTotalAggregatedExpense(TEST_AGGREGATED_EXPENDITURE);
    }

    public static AuthorizationResponseDTO getAuthorizationResponseDTO(boolean valid) {
        return AuthorizationResponseDTO.builder().result(valid).build();
    }

    public static OutstandingAssessmentResultDTO getOutstandingAssessmentResultDTO(boolean outstandingAssessmentsFound) {
        return OutstandingAssessmentResultDTO.builder().outstandingAssessments(outstandingAssessmentsFound).build();
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
                .withId(TEST_DETAIL_ID)
                .withCriteriaDetailId(TEST_CRITERIA_DETAIL_ID)
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

    public static MaatApiAssessmentResponse getMaatApiAssessmentResponse() {
        return new MaatApiAssessmentResponse()
                .withInitResult("PASS")
                .withInitResultReason("Gross income below the lower threshold")
                .withAssessmentDetails(getApiAssessmentDetails())
                .withChildWeightings(getAssessmentChildWeightings());
    }

    public static ApiCreateMeansAssessmentRequest getCreateMeansAssessmentRequest(boolean isValid) {
        return new ApiCreateMeansAssessmentRequest()
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withReviewType(ReviewType.NAFI)
                .withRepId(isValid ? 91919 : null)
                .withCmuId(isValid ? 91919 : null)
                .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withNewWorkReason(NewWorkReason.PBI)
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withCaseType(CaseType.EITHER_WAY)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getListOfAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withEmploymentStatus(TEST_EMPLOYMENT_STATUS)
                .withUsn(TEST_USN)
                .withTimestamp(TEST_DATE_CREATED)
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withSectionSummaries(List.of(getAssessmentSectionSummary(Section.INITB.name(), AssessmentType.INIT)));
    }


    public static ApiUpdateMeansAssessmentRequest getUpdateMeansAssessmentRequest(boolean isValid) {
        return new ApiUpdateMeansAssessmentRequest()
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withRepId(isValid ? 91919 : null)
                .withCmuId(isValid ? 91919 : null)
                .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withFullAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withOtherHousingNote(TEST_NOTE)
                .withInitTotalAggregatedIncome(TEST_AGGREGATED_INCOME)
                .withFullAssessmentNotes(TEST_NOTE)
                .withCaseType(CaseType.EITHER_WAY)
                .withEmploymentStatus(TEST_EMPLOYMENT_STATUS)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getListOfAssessmentChildWeightings())
                .withUserSession(getUserSession())
                .withTimestamp(TEST_DATE_CREATED)
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withSectionSummaries(List.of(getAssessmentSectionSummary(Section.INITB.name(), AssessmentType.INIT)));
    }


    public static ApiAssessmentSectionSummary getAssessmentSectionSummary(String section, AssessmentType assessmentType) {
        return new ApiAssessmentSectionSummary()
                .withApplicantAnnualTotal(TEST_APPLICANT_ANNUAL_TOTAL)
                .withAnnualTotal(TEST_APPLICANT_ANNUAL_TOTAL)
                .withPartnerAnnualTotal(BigDecimal.ZERO)
                .withSection(section)
                .withAssessmentType(assessmentType)
                .withAssessmentDetails(
                        new ArrayList<>(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(TEST_CRITERIA_DETAIL_ID)
                                                .withApplicantAmount(TEST_APPLICANT_VALUE)
                                                .withApplicantFrequency(TEST_FREQUENCY)
                                )
                        )
                );
    }

    public static List<ApiAssessmentChildWeighting> getListOfAssessmentChildWeightings() {
        return List.of(
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(37)
                        .withNoOfChildren(1)
                ,
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(38)
                        .withNoOfChildren(2),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(39)
                        .withNoOfChildren(2),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(40)
                        .withNoOfChildren(2),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(41)
                        .withNoOfChildren(2),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(42)
                        .withNoOfChildren(2),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(43)
                        .withNoOfChildren(2)
        );
    }

    public static FinancialAssessmentDTO getFinancialAssessmentDTOWithChildWeightings() {
        FinancialAssessmentDTO financialAssessment = getFinancialAssessmentDTO();
        financialAssessment.setChildWeightings(getChildWeightings());
        return financialAssessment;
    }

    public static List<ChildWeightings> getChildWeightings() {
        List<ChildWeightings> childWeightingsList = new ArrayList<>();
        ChildWeightings childWeightings = new ChildWeightings();
        childWeightings.setChildWeightingId(37);
        childWeightings.setNoOfChildren(2);
        childWeightings.setId(1234);
        childWeightingsList.add(childWeightings);
        return childWeightingsList;

    }

    public static FinancialAssessmentDTO getFinancialAssessmentDTOWithDetails() {
        FinancialAssessmentDTO financialAssessment = getFinancialAssessmentDTO();
        financialAssessment.setAssessmentDetails(getAssessmentDetails());
        return financialAssessment;
    }

    public static FinancialAssessmentDTO getFinancialAssessmentDTO() {
        return FinancialAssessmentDTO.builder()
                .id(TEST_FINANCIAL_ASSESSMENT_ID)
                .cmuId(CMU_ID)
                .initialAscrId(1)
                .repId(TEST_REP_ID)
                .userCreated(TEST_USER)
                .assessmentType(AssessmentType.INIT.getType())
                .newWorkReason(NewWorkReason.CFC.getCode())
                .dateCreated(TEST_DATE_CREATED)
                .fassInitStatus(CurrentStatus.COMPLETE.getStatus())
                .initialAssessmentDate(LocalDateTime.parse("2021-10-09T15:02:25"))
                .initTotAggregatedIncome(BigDecimal.valueOf(15600.00))
                .initAdjustedIncomeValue(BigDecimal.valueOf(15600.00))
                .initResult(InitAssessmentResult.PASS.getResult())
                .initApplicationEmploymentStatus(TEST_EMPLOYMENT_STATUS)
                .build();
    }

    public static AssessmentCriteriaDetailEntity getAssessmentCriteriaDetailEntity(String section) {
        return AssessmentCriteriaDetailEntity.builder()
                .id(TEST_DETAIL_ID)
                .description(TEST_DESCRIPTION)
                .section(TEST_SECTION)
                .seq(TEST_SEQ)
                .createdDateTime(LocalDateTime.now())
                .createdBy(TEST_USER)
                .modifiedDateTime(LocalDateTime.now())
                .modifiedBy(TEST_USER)
                .assessmentDetail(AssessmentDetailEntity.builder().detailCode(TEST_DETAIL_CODE).build())
                .build();
    }

    public static AssessmentDTO getAssessmentDTO(String section, Integer sequence) {
        return AssessmentDTO.builder().applicantAmount(BigDecimal.valueOf(10.00))
                .applicantFrequency(Frequency.MONTHLY)
                .partnerAmount(BigDecimal.valueOf(20.00))
                .partnerFrequency(Frequency.ANNUALLY)
                .criteriaDetailId(TEST_ASSESSMENT_DETAILS_ID)
                .dateModified(LocalDateTime.now())
                .section(section)
                .sequence(sequence)
                .criteriaDetailDescription(TEST_DESCRIPTION).build();
    }

    public static List<FinancialAssessmentDetails> getAssessmentDetails() {

        return List.of(
                FinancialAssessmentDetails.builder()
                        .criteriaDetailId(TEST_CRITERIA_DETAIL_ID)
                        .applicantAmount(BigDecimal.valueOf(1650.00))
                        .applicantFrequency(Frequency.MONTHLY)
                        .partnerAmount(BigDecimal.valueOf(1650.00))
                        .partnerFrequency(Frequency.TWO_WEEKLY)
                        .build(),
                FinancialAssessmentDetails.builder()
                        .criteriaDetailId(131)
                        .applicantAmount(BigDecimal.valueOf(200.00))
                        .applicantFrequency(Frequency.ANNUALLY)
                        .partnerAmount(BigDecimal.valueOf(10.00))
                        .partnerFrequency(Frequency.ANNUALLY)
                        .build()
        );
    }

    public static FinancialAssessmentDetails getAssessmentDetailsWithoutList() {
        return FinancialAssessmentDetails.builder()
                .criteriaDetailId(TEST_ASSESSMENT_DETAILS_ID)
                .applicantAmount(BigDecimal.valueOf(1650.00))
                .applicantFrequency(Frequency.MONTHLY)
                .partnerAmount(BigDecimal.valueOf(1650.00))
                .partnerFrequency(Frequency.TWO_WEEKLY)
                .id(TEST_DETAIL_ID)
                .build();
    }

    public static RepOrderDTO getRepOrderDTO() {
        return RepOrderDTO.builder()
                .id(TEST_REP_ID)
                .catyCaseType(CaseType.EITHER_WAY.getCaseType())
                .magsOutcome(MagCourtOutcome.COMMITTED.getOutcome())
                .magsOutcomeDate(TEST_MAGS_OUTCOME_DATE.toString())
                .magsOutcomeDateSet(TEST_MAGS_OUTCOME_DATE)
                .committalDate(TEST_MAGS_OUTCOME_DATE)
                .repOrderDecisionReasonCode("rder-code")
                .crownRepOrderDecision("cc-rep-doc")
                .crownRepOrderType("cc-rep-type")
                .build();
    }

    public static RepOrderDTO getRepOrderDTOWithAssessments(List<FinancialAssessmentDTO> financialAssessments) {
        RepOrderDTO repOrderDTO = getRepOrderDTO();
        repOrderDTO.setFinancialAssessments(financialAssessments);
        return repOrderDTO;
    }

    public static FinAssIncomeEvidenceDTO getFinAssIncomeEvidenceDTO(String mandatory, String evidence) {
        return FinAssIncomeEvidenceDTO.builder()
                .incomeEvidence(evidence)
                .mandatory(mandatory)
                .adhoc("Y")
                .dateModified(LocalDateTime.now())
                .id(1234)
                .dateCreated(LocalDateTime.now())
                .dateReceived(LocalDateTime.now())
                .removedDate(LocalDate.now())
                .active("Y")
                .otherText("Other")
                .userCreated(TEST_USER)
                .applicant(getApplicantDTO())
                .build();
    }

    public static ApplicantDTO getApplicantDTO() {
        return ApplicantDTO.builder()
                .id(12)
                .build();
    }

    public static FinancialAssessmentDTO getFinancialAssessmentDTOWithIncomeEvidence() {
        FinancialAssessmentDTO financialAssessment = getFinancialAssessmentDTO();
        List<FinAssIncomeEvidenceDTO> finAssIncomeEvidenceDTOList = new ArrayList<>();
        finAssIncomeEvidenceDTOList.add(getFinAssIncomeEvidenceDTO("Y", "SIGNATURE"));
        financialAssessment.setFinAssIncomeEvidences(finAssIncomeEvidenceDTOList);
        return financialAssessment;
    }

    public static FinancialAssessmentDTO getFinancialAssessmentDTO(String status, String newWorkReason, String reviewType) {
        return FinancialAssessmentDTO.builder()
                .id(TEST_FINANCIAL_ASSESSMENT_ID)
                .cmuId(CMU_ID)
                .initialAscrId(TEST_CRITERIA_ID)
                .fullAscrId(TEST_CRITERIA_ID)
                .initOtherBenefitNote(TEST_NOTE)
                .initOtherIncomeNote(TEST_INCOME_NOTE)
                .initTotAggregatedIncome(BigDecimal.valueOf(15600.00))
                .initTotAggregatedIncome(BigDecimal.valueOf(15600.00))
                .initNotes(TEST_NOTE)
                .initResult(InitAssessmentResult.FULL.name())
                .initResultReason(InitAssessmentResult.PASS.getReason())
                .fassInitStatus(status)
                .newWorkReason(newWorkReason)
                .rtCode(reviewType)
                .repId(TEST_REP_ID)
                .userCreated(TEST_USER)
                .assessmentType(AssessmentType.INIT.getType())
                .dateCreated(TEST_DATE_CREATED)
                .initialAssessmentDate(LocalDateTime.parse("2021-10-09T15:02:25"))
                .fullAssessmentDate(LocalDateTime.parse("2022-10-09T15:02:25"))
                .fullAssessmentNotes(TEST_NOTE)
                .fullAdjustedLivingAllowance(BigDecimal.valueOf(15600.00))
                .fullOtherHousingNote(TEST_NOTE)
                .fullTotalAggregatedExpenses(BigDecimal.valueOf(22000.00))
                .fullTotalAnnualDisposableIncome(BigDecimal.valueOf(1000.00))
                .fullResult(FullAssessmentResult.PASS.getResult())
                .fullResultReason(FullAssessmentResult.PASS.getReason())
                .fassFullStatus(status)
                .initTotAggregatedIncome(BigDecimal.valueOf(15600.00))
                .initAdjustedIncomeValue(BigDecimal.valueOf(15600.00))
                .initResult(InitAssessmentResult.PASS.getResult())
                .initApplicationEmploymentStatus(TEST_EMPLOYMENT_STATUS)
                .firstIncomeReminderDate(LocalDateTime.parse("2021-10-09T15:02:25"))
                .secondIncomeReminderDate(LocalDateTime.parse("2021-10-09T15:02:25"))
                .build();
    }

    public static ApiInitialMeansAssessment getApiInitialMeansAssessment(CurrentStatus currentStatus, NewWorkReason newWorkReason,
                                                                         ReviewType reviewType) {
        ApiInitialMeansAssessment apiInitialMeansAssessment = new ApiInitialMeansAssessment();
        apiInitialMeansAssessment.setId(TEST_CRITERIA_ID);
        apiInitialMeansAssessment.setAssessmentDate(LocalDateTime.parse("2021-10-09T15:02:25"));
        apiInitialMeansAssessment.setOtherBenefitNote(TEST_NOTE);
        apiInitialMeansAssessment.setOtherIncomeNote(TEST_INCOME_NOTE);
        apiInitialMeansAssessment.setTotalAggregatedIncome(BigDecimal.valueOf(15600.00));
        apiInitialMeansAssessment.setAdjustedIncomeValue(BigDecimal.valueOf(15600.00));
        apiInitialMeansAssessment.setNotes(TEST_NOTE);
        apiInitialMeansAssessment.setLowerThreshold(BigDecimal.valueOf(12500.0));
        apiInitialMeansAssessment.setUpperThreshold(BigDecimal.valueOf(22500.0));
        apiInitialMeansAssessment.setResult(InitAssessmentResult.PASS.getResult());
        apiInitialMeansAssessment.setResultReason(InitAssessmentResult.PASS.getReason());
        if (null !=currentStatus) {
            ApiAssessmentStatus apiAssessmentStatus = new ApiAssessmentStatus();
            apiAssessmentStatus.setStatus(currentStatus.getStatus());
            apiAssessmentStatus.setDescription(currentStatus.getDescription());
            apiInitialMeansAssessment.setAssessmentStatus(apiAssessmentStatus);
        }

        if (null != newWorkReason) {
            ApiNewWorkReason apiNewWorkReason = new ApiNewWorkReason();
            apiNewWorkReason.setCode(newWorkReason.getCode());
            apiNewWorkReason.setDescription(newWorkReason.getDescription());
            apiNewWorkReason.setType(newWorkReason.getType());
            apiInitialMeansAssessment.setNewWorkReason(apiNewWorkReason);
        }

        if (null != reviewType) {
            ApiReviewType rType = new ApiReviewType();
            rType.setCode(reviewType.getCode());
            rType.setDescription(reviewType.getDescription());
            apiInitialMeansAssessment.setReviewType(rType);
        }

        return  apiInitialMeansAssessment;
    }

    public static ApiFullMeansAssessment getApiFullAssessment(CurrentStatus currentStatus) {
        ApiFullMeansAssessment apiFullMeansAssessment = new ApiFullMeansAssessment();
        apiFullMeansAssessment.setId(TEST_CRITERIA_ID);
        apiFullMeansAssessment.setAssessmentDate(LocalDateTime.parse("2022-10-09T15:02:25"));
        apiFullMeansAssessment.setAssessmentNotes(TEST_NOTE);
        apiFullMeansAssessment.setAdjustedLivingAllowance(BigDecimal.valueOf(15600.00));
        apiFullMeansAssessment.setOtherHousingNote(TEST_NOTE);
        apiFullMeansAssessment.setTotalAggregatedExpense(BigDecimal.valueOf(22000.00));
        apiFullMeansAssessment.setTotalAnnualDisposableIncome(BigDecimal.valueOf(1000.00));
        apiFullMeansAssessment.setThreshold(BigDecimal.valueOf(5000.00));
        apiFullMeansAssessment.setResult(FullAssessmentResult.PASS.getResult());
        apiFullMeansAssessment.setResultReason(FullAssessmentResult.PASS.getReason());
        if (null !=currentStatus) {
            ApiAssessmentStatus apiAssessmentStatus = new ApiAssessmentStatus();
            apiAssessmentStatus.setStatus(currentStatus.getStatus());
            apiAssessmentStatus.setDescription(currentStatus.getDescription());
            apiFullMeansAssessment.setAssessmentStatus(apiAssessmentStatus);
        }
        return apiFullMeansAssessment;
    }

}