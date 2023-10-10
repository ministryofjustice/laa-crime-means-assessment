package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.IncomeType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.OutgoingType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.justice.laa.crime.meansassessment.service.stateless.MaatToStatelessDataAdapter.*;

class MaatToStatelessDataAdapterTest {

    private static final AssessmentCriteriaEntity assessmentCriteria = TestModelDataBuilder.getAssessmentCriteriaEntityWithChildWeightings(
            new BigDecimal[]{
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.35)
            }
    );

    @Test
    void childGroupingsFromChildWeightingsReturnsMapOfRangesToQuantities() {
        assertThat(childGroupingsFromChildWeightings(Arrays.asList(
                new ApiAssessmentChildWeighting()
                        .withNoOfChildren(1)
                        .withLowerAgeRange(2)
                        .withUpperAgeRange(4),
                new ApiAssessmentChildWeighting()
                        .withNoOfChildren(2)
                        .withLowerAgeRange(5)
                        .withUpperAgeRange(7)))
        ).isEqualTo(Map.of(AgeRange.TWO_TO_FOUR, 1, AgeRange.FIVE_TO_SEVEN, 2));
    }

    @Test
    void incomesFromSectionSummariesConvertsIntoCorrectIncomeTypesAndAmounts() {
        assertThat(incomesFromSectionSummaries(
                Arrays.asList(new ApiAssessmentSectionSummary()
                        .withAssessmentDetails(
                                Arrays.asList(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(135)
                                                .withApplicantAmount(BigDecimal.TEN)
                                                .withApplicantFrequency(Frequency.WEEKLY)))),
                assessmentCriteria))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(
                        Arrays.asList(
                                new Income(IncomeType.EMPLOYMENT_INCOME,
                                        new FrequencyAmount(Frequency.WEEKLY, BigDecimal.TEN),
                                        null)));
    }

    @Test
    void outgoingsFromSectionSummariesConvertsIntoCorrectOutgoingTypesAndAmounts() {
        assertThat(outgoingsFromSectionSummaries(
                Arrays.asList(new ApiAssessmentSectionSummary()
                        .withAssessmentDetails(
                                Arrays.asList(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(128)
                                                .withApplicantAmount(BigDecimal.TEN)
                                                .withApplicantFrequency(Frequency.MONTHLY)))),
                assessmentCriteria))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(
                        Arrays.asList(
                                new Outgoing(OutgoingType.TAX,
                                        new FrequencyAmount(Frequency.MONTHLY, BigDecimal.TEN),
                                        null)));
    }
}