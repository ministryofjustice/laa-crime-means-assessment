package uk.gov.justice.laa.crime.meansassessment.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.AuthorizationResponseDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.OutstandingAssessmentResultDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
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
    public static final Integer TEST_DETAIL_ID = 135;
    public static final String TEST_DETAIL_CODE = "TEST_CODE";
    public static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    public static final String TEST_SECTION = "SECTION";
    public static final Integer TEST_SEQ = 10;

    public static final String TEST_EMPLOYMENT_STATUS = "EMPLOY";
    public static final Integer TEST_USN = 4056595;
    public static final String TEST_NOTE = "TEST_NOTE";

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

    public static final BigDecimal TEST_DISPOSABLE_INCOME = BigDecimal.valueOf(4000d);
    public static final BigDecimal TEST_ADJUSTED_LIVING_ALLOWANCE = BigDecimal.valueOf(6000d);
    public static final BigDecimal TEST_AGGREGATED_EXPENDITURE = BigDecimal.valueOf(2000d);
    public static final BigDecimal TEST_AGGREGATED_INCOME = BigDecimal.valueOf(12000d);
    public static final BigDecimal TEST_ADJUSTED_INCOME = BigDecimal.valueOf(20000d);

    public static final int MEANS_ASSESSMENT_ID = 1000;
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";

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
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withMagCourtOutcome(MagCourtOutcome.COMMITTED)
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
                .newWorkReason(NewWorkReason.PBI)
                .fullAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
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
                                                .withCriteriaDetailId(132)
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
                .withUserName("test-userid");
    }

    public static List<ApiAssessmentChildWeighting> getAssessmentChildWeightings() {
        return List.of(
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(37)
                        .withNoOfChildren(1)
                ,
                new ApiAssessmentChildWeighting()
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
                .withFassFullStatus(isValid ? CurrentStatus.COMPLETE: null)
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