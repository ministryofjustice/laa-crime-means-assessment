package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.enums.meansassessment.AgeRange;
import uk.gov.justice.laa.crime.enums.meansassessment.IncomeType;
import uk.gov.justice.laa.crime.enums.meansassessment.OutgoingType;
import uk.gov.justice.laa.crime.meansassessment.FrequencyAmount;
import uk.gov.justice.laa.crime.meansassessment.Income;
import uk.gov.justice.laa.crime.meansassessment.Outgoing;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentDetailEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StatelessDataAdaptorTest {


    private final AssessmentCriteriaEntity assessmentCriteria = TestModelDataBuilder.getAssessmentCriteriaEntity();
    private Set<AssessmentCriteriaChildWeightingEntity> childWeightingEntities;

    @BeforeEach
    void setup() {
        assessmentCriteria.setAssessmentCriteriaDetails(
                Set.of(
                        AssessmentCriteriaDetailEntity.builder()
                                .id(135)
                                .section("INITB")
                                .assessmentDetail(
                                        AssessmentDetailEntity.builder()
                                                .detailCode("EMP_INC")
                                                .build())
                                .build(),
                        AssessmentCriteriaDetailEntity.builder()
                                .id(136)
                                .section("INITA")
                                .assessmentDetail(
                                        AssessmentDetailEntity.builder()
                                                .detailCode("SELF_EMP")
                                                .build())
                                .build(),
                        AssessmentCriteriaDetailEntity.builder()
                                .id(137)
                                .section("FULLA")
                                .assessmentDetail(
                                        AssessmentDetailEntity.builder()
                                                .detailCode("COUNCIL")
                                                .build())
                                .build(),
                        AssessmentCriteriaDetailEntity.builder()
                                .id(138)
                                .section("FULLB")
                                .assessmentDetail(
                                        AssessmentDetailEntity.builder()
                                                .detailCode("NI")
                                                .build())
                                .build()
                )
        );

        childWeightingEntities = Set.of(
                AssessmentCriteriaChildWeightingEntity.builder()
                        .id(1)
                        .lowerAgeRange(0)
                        .upperAgeRange(1)
                        .weightingFactor(BigDecimal.valueOf(0.15))
                        .build(),
                AssessmentCriteriaChildWeightingEntity.builder()
                        .id(2)
                        .lowerAgeRange(2)
                        .upperAgeRange(4)
                        .weightingFactor(BigDecimal.valueOf(0.25))
                        .build(),
                AssessmentCriteriaChildWeightingEntity.builder()
                        .id(3)
                        .lowerAgeRange(5)
                        .upperAgeRange(7)
                        .weightingFactor(BigDecimal.valueOf(0.35))
                        .build()
        );

    }

    @Test
    void givenValidApplicantIncomes_whenMapIncomesToSectionSummariesIsInvoked_thenSectionSummariesAreReturned() {
        List<Income> incomes = List.of(
                buildIncome(IncomeType.EMPLOYMENT_INCOME, BigDecimal.valueOf(1500), Frequency.MONTHLY),
                buildIncome(IncomeType.SELF_EMPLOYMENT_INCOME, BigDecimal.valueOf(500), Frequency.ANNUALLY)
        );

        var result = StatelessDataAdapter.mapIncomesToSectionSummaries(assessmentCriteria, incomes);

        var expected = List.of(new ApiAssessmentSectionSummary()
                        .withSection("INITB")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(135)
                                                .withApplicantAmount(BigDecimal.valueOf(1500))
                                                .withApplicantFrequency(Frequency.MONTHLY)
                                )
                        ),
                new ApiAssessmentSectionSummary()
                        .withSection("INITA")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(136)
                                                .withApplicantAmount(BigDecimal.valueOf(500))
                                                .withApplicantFrequency(Frequency.ANNUALLY)
                                )
                        )
        );

        assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void givenValidApplicantAndPartnerIncomes_whenMapIncomesToSectionSummariesIsInvoked_thenSectionSummariesAreReturned() {
        List<Income> incomes = List.of(
                buildIncome(IncomeType.EMPLOYMENT_INCOME,
                        BigDecimal.valueOf(1500),
                        Frequency.MONTHLY,
                        BigDecimal.valueOf(200),
                        Frequency.WEEKLY
                ),
                buildIncome(IncomeType.SELF_EMPLOYMENT_INCOME,
                        BigDecimal.valueOf(500),
                        Frequency.ANNUALLY,
                        BigDecimal.valueOf(50),
                        Frequency.MONTHLY
                )
        );

        var result = StatelessDataAdapter.mapIncomesToSectionSummaries(assessmentCriteria, incomes);

        var expected = List.of(new ApiAssessmentSectionSummary()
                        .withSection("INITB")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(135)
                                                .withApplicantAmount(BigDecimal.valueOf(1500))
                                                .withApplicantFrequency(Frequency.MONTHLY)
                                                .withPartnerAmount(BigDecimal.valueOf(200))
                                                .withPartnerFrequency(Frequency.WEEKLY)
                                )
                        ),
                new ApiAssessmentSectionSummary()
                        .withSection("INITA")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(136)
                                                .withApplicantAmount(BigDecimal.valueOf(500))
                                                .withApplicantFrequency(Frequency.ANNUALLY)
                                                .withPartnerAmount(BigDecimal.valueOf(50))
                                                .withPartnerFrequency(Frequency.MONTHLY)
                                )
                        )
        );

        assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void givenValidApplicantOutgoings_whenMapOutgoingsToSectionSummariesIsInvoked_thenSectionSummariesAreReturned() {
        List<Outgoing> outgoings = List.of(
                buildOutgoing(OutgoingType.COUNCIL_TAX, BigDecimal.valueOf(500), Frequency.ANNUALLY),
                buildOutgoing(OutgoingType.NATIONAL_INSURANCE, BigDecimal.valueOf(1500), Frequency.MONTHLY)
        );

        var result = StatelessDataAdapter.mapOutgoingsToSectionSummaries(assessmentCriteria, outgoings);

        var expected = List.of(new ApiAssessmentSectionSummary()
                        .withSection("FULLA")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(137)
                                                .withApplicantAmount(BigDecimal.valueOf(500))
                                                .withApplicantFrequency(Frequency.ANNUALLY)
                                )
                        ),
                new ApiAssessmentSectionSummary()
                        .withSection("FULLB")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(138)
                                                .withApplicantAmount(BigDecimal.valueOf(1500))
                                                .withApplicantFrequency(Frequency.MONTHLY)
                                )
                        )
        );

        assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void givenValidApplicantAndPartnerOutgoings_whenMapOutgoingsToSectionSummariesIsInvoked_thenSectionSummariesAreReturned() {
        List<Outgoing> outgoings = List.of(
                buildOutgoing(OutgoingType.COUNCIL_TAX,
                        BigDecimal.valueOf(500),
                        Frequency.ANNUALLY,
                        BigDecimal.valueOf(200),
                        Frequency.MONTHLY
                ),
                buildOutgoing(OutgoingType.NATIONAL_INSURANCE,
                        BigDecimal.valueOf(1500),
                        Frequency.MONTHLY,
                        BigDecimal.valueOf(3000),
                        Frequency.ANNUALLY
                )
        );

        var result = StatelessDataAdapter.mapOutgoingsToSectionSummaries(assessmentCriteria, outgoings);

        var expected = List.of(new ApiAssessmentSectionSummary()
                        .withSection("FULLA")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(137)
                                                .withApplicantAmount(BigDecimal.valueOf(500))
                                                .withApplicantFrequency(Frequency.ANNUALLY)
                                                .withPartnerAmount(BigDecimal.valueOf(200))
                                                .withPartnerFrequency(Frequency.MONTHLY)

                                )
                        ),
                new ApiAssessmentSectionSummary()
                        .withSection("FULLB")
                        .withAssessmentDetails(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(138)
                                                .withApplicantAmount(BigDecimal.valueOf(1500))
                                                .withApplicantFrequency(Frequency.MONTHLY)
                                                .withPartnerAmount(BigDecimal.valueOf(3000))
                                                .withPartnerFrequency(Frequency.ANNUALLY)
                                )
                        )
        );

        assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void givenValidWeightsAndNoGroupings_whenMapChildGroupingsIsInvoked_thenMappingIsPerformed() {

        List<ApiAssessmentChildWeighting> expected = List.of(
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(1)
                        .withNoOfChildren(0),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(2)
                        .withNoOfChildren(0),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(3)
                        .withNoOfChildren(0)
        );

        var result = StatelessDataAdapter.mapChildGroupings(Collections.emptyMap(), childWeightingEntities);
        assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    @Test
    void givenValidWeightsAndGroupings_whenMapChildGroupingsIsInvoked_thenMappingIsPerformed() {

        Map<AgeRange, Integer> childGroupings = Map.of(
                AgeRange.ZERO_TO_ONE, 1,
                AgeRange.TWO_TO_FOUR, 0,
                AgeRange.FIVE_TO_SEVEN, 2
        );

        List<ApiAssessmentChildWeighting> expected = List.of(
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(1)
                        .withNoOfChildren(1),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(2)
                        .withNoOfChildren(0),
                new ApiAssessmentChildWeighting()
                        .withChildWeightingId(3)
                        .withNoOfChildren(2)
        );

        var result = StatelessDataAdapter.mapChildGroupings(childGroupings, childWeightingEntities);
        assertThat(result).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
    }

    private Income buildIncome(IncomeType incomeType, BigDecimal applicantAmount, Frequency applicantFrequency) {
        return buildIncome(incomeType, applicantAmount, applicantFrequency, null, null);
    }

    private Income buildIncome(IncomeType incomeType,
                               BigDecimal applicantAmount,
                               Frequency applicantFrequency,
                               BigDecimal partnerAmount,
                               Frequency partnerFrequency) {

        Income.IncomeBuilder builder = Income.builder()
                .incomeType(incomeType)
                .applicant(
                        FrequencyAmount.builder()
                                .amount(applicantAmount)
                                .frequency(applicantFrequency)
                                .build()
                );

        if (null != partnerAmount) {
            builder
                    .partner(
                            FrequencyAmount.builder()
                                    .amount(partnerAmount)
                                    .frequency(partnerFrequency)
                                    .build()
                    );
        }
        return builder.build();
    }

    private Outgoing buildOutgoing(OutgoingType outgoingType, BigDecimal applicantAmount, Frequency applicantFrequency) {
        return buildOutgoing(outgoingType, applicantAmount, applicantFrequency, null, null);
    }

    private Outgoing buildOutgoing(OutgoingType outgoingType,
                                   BigDecimal applicantAmount,
                                   Frequency applicantFrequency,
                                   BigDecimal partnerAmount,
                                   Frequency partnerFrequency) {

        Outgoing.OutgoingBuilder builder = Outgoing.builder()
                .outgoingType(outgoingType)
                .applicant(
                        FrequencyAmount.builder()
                                .amount(applicantAmount)
                                .frequency(applicantFrequency)
                                .build()
                );

        if (null != partnerAmount) {
            builder
                    .partner(
                            FrequencyAmount.builder()
                                    .amount(partnerAmount)
                                    .frequency(partnerFrequency)
                                    .build()
                    );
        }
        return builder.build();
    }
}
