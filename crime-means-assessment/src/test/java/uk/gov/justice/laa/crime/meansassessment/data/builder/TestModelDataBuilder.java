package uk.gov.justice.laa.crime.meansassessment.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // Assessment Criteria Child Weighting
    public static final BigDecimal TEST_INITIAL_LOWER_AGE_RANGE =  BigDecimal.valueOf(3d);
    public static final BigDecimal TEST_INITIAL_UPPER_AGE_RANGE =  BigDecimal.valueOf(5d);
    public static final BigDecimal TEST_WEIGHTING_FACTOR = BigDecimal.ONE;

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
}

