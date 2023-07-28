package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.IncomeType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.OutgoingType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class DataAdapter {
    private static final Map<IncomeType, String> inputToDetailCodeMap = Map.ofEntries(
            Map.entry(IncomeType.EMPLOYMENT_INCOME, "EMP_INC"),
            Map.entry(IncomeType.HOUSING_BENEFIT, "HOUSE_BEN"),
            Map.entry(IncomeType.OTHER_INCOME, "OTHER_INC"),
            Map.entry(IncomeType.INCOME_FROM_SAVINGS_OR_WITHDRAWALS, "SAVINGS"),
            Map.entry(IncomeType.SELF_EMPLOYMENT_INCOME, "SELF_EMP"),
            Map.entry(IncomeType.BENEFITS_FROM_WORK, "WORK_BEN"),
            Map.entry(IncomeType.CHILD_BENEFIT, "CHILD_BEN"),
            Map.entry(IncomeType.WORKING_TAX_CREDITS, "TAX_CRED"),
            Map.entry(IncomeType.INCAPACITY_BENEFIT, "INCAP_BEN"),
            Map.entry(IncomeType.STATE_PENSION, "STATE_PEN"),
            Map.entry(IncomeType.INDUSTRIAL_INJURIES_DISABLEMENT_BENEFIT, "INJ_BEN"),
            Map.entry(IncomeType.OTHER_BENEFITS, "OTHER_BEN"),
            Map.entry(IncomeType.PRIVATE_PENSION, "PRIV_PENS"),
            Map.entry(IncomeType.MAINTENANCE_INCOME, "MAINT_INC")
    );

    private static final Map<OutgoingType, String> outgoingToDetailCodeMap = Map.of(
            OutgoingType.TAX, "TAX",
            OutgoingType.RENT_OR_MORTGAGE, "RENT_MORT",
            OutgoingType.COUNCIL_TAX, "COUNCIL",
            OutgoingType.NATIONAL_INSURANCE, "NI",
            OutgoingType.CHILDCARE_COSTS, "CHILD_COST",
            OutgoingType.MAINTENANCE_COSTS, "MAINT_COST",
            OutgoingType.OTHER_HOUSING_FEES, "OTHER_HOUS"
    );

    private static final Map<String, List<IncomeType>> initSectionMapping = Map.of(
            "INITA", List.of(IncomeType.OTHER_INCOME, IncomeType.INCOME_FROM_SAVINGS_OR_WITHDRAWALS, IncomeType.SELF_EMPLOYMENT_INCOME),
            "INITB", List.of(IncomeType.EMPLOYMENT_INCOME, IncomeType.BENEFITS_FROM_WORK, IncomeType.CHILD_BENEFIT,
                    IncomeType.WORKING_TAX_CREDITS, IncomeType.HOUSING_BENEFIT,
                    IncomeType.INCAPACITY_BENEFIT, IncomeType.STATE_PENSION, IncomeType.INDUSTRIAL_INJURIES_DISABLEMENT_BENEFIT,
                    IncomeType.OTHER_BENEFITS, IncomeType.PRIVATE_PENSION, IncomeType.MAINTENANCE_INCOME)
    );

    private static final Map<String, List<OutgoingType>> fullSectionMapping = Map.of(
            "FULLA", List.of(OutgoingType.RENT_OR_MORTGAGE, OutgoingType.COUNCIL_TAX, OutgoingType.OTHER_HOUSING_FEES),
            "FULLB", List.of(OutgoingType.TAX, OutgoingType.NATIONAL_INSURANCE, OutgoingType.CHILDCARE_COSTS, OutgoingType.MAINTENANCE_COSTS)
    );

    public static List<ApiAssessmentSectionSummary> incomeSectionSummaries(AssessmentCriteriaEntity assessmentCriteria,
                                                                           @NotNull List<Income> incomes) {
        return incomes.stream().map(income -> {
            var detailCode = inputToDetailCodeMap.get(income.getIncomeType());
            var detail = createAssessmentDetail(income, assessmentCriteria, detailCode);
            return new ApiAssessmentSectionSummary().
                    withSection(incomeSection(income.getIncomeType())).
                    withAssessmentDetails(Collections.singletonList(detail));
        }).toList();
    }

    public static List<ApiAssessmentSectionSummary> outgoingSectionSummaries(AssessmentCriteriaEntity assessmentCriteria,
                                                                             @NotNull List<Outgoing> outgoings) {
        return outgoings.stream().map(outgoing -> {
            var detailCode = outgoingToDetailCodeMap.get(outgoing.getOutgoingType());
            var detail = createAssessmentDetail(outgoing, assessmentCriteria, detailCode);
            return new ApiAssessmentSectionSummary().
                    withSection(outgoingSection(outgoing.getOutgoingType())).
                    withAssessmentDetails(Collections.singletonList(detail));
        }).toList();
    }

    private static ApiAssessmentDetail createAssessmentDetail(Amount amount,
                                                              AssessmentCriteriaEntity assessmentCriteria, String detailCode) {
        // create a map based on detailCode, so that entity can be found trivially with a get() call
        var detailCodeToDetailEntity = assessmentCriteria.getAssessmentCriteriaDetails().stream()
                .collect(Collectors.toMap(acd -> acd.getAssessmentDetail().getDetailCode(), Function.identity()));

        var detailEntity = detailCodeToDetailEntity.get(detailCode);
        var applicant = amount.getApplicant();
        var partner = amount.getPartner();
        var detail = new ApiAssessmentDetail()
                .withCriteriaDetailId(detailEntity.getId())
                .withApplicantAmount(applicant.getAmount())
                .withApplicantFrequency(applicant.getFrequency());
        if (partner != null) {
            detail = detail.withPartnerAmount(partner.getAmount())
                    .withPartnerFrequency(partner.getFrequency());
        }
        return detail;
    }

    public static List<ApiAssessmentChildWeighting> convertChildGroupings(Map<AgeRange, Integer> childGroupings,
                                                                          Set<AssessmentCriteriaChildWeightingEntity> childWeightings) {
        final var children = childGroupings
                .entrySet()
                .stream().map(childGroup -> {
                    var weighting = childWeightings
                            .stream()
                            .filter(weightingEntity -> {
                                final AgeRange grouping = childGroup.getKey();
                                return weightingEntity.getLowerAgeRange() == grouping.getLowerLimit() &&
                                        weightingEntity.getUpperAgeRange() == grouping.getUpperLimit();
                            })
                            .findFirst();
                    return new ApiAssessmentChildWeighting()
                            .withChildWeightingId(weighting.get().getId())
                            .withNoOfChildren(childGroup.getValue());
                }).collect(Collectors.toList());
        final var childIds = children.stream().map(ApiAssessmentChildWeighting::getChildWeightingId).collect(Collectors.toSet());
        final var zeroes = childWeightings
                .stream()
                .filter(x -> !childIds.contains(x.getId()))
                .map(x -> new ApiAssessmentChildWeighting()
                        .withChildWeightingId(x.getId())
                        .withNoOfChildren(0)).toList();
        // Service requires non-present groups to be filled in with zero values
        children.addAll(zeroes);
        return children;
    }

    // find section name from IncomeType
    private static String incomeSection(IncomeType incomeType) {
        var section = initSectionMapping
                .entrySet()
                .stream()
                .filter(sectionMap -> sectionMap.getValue().contains(incomeType))
                .findFirst();
        return section.map(Map.Entry::getKey).orElseThrow(() -> new RuntimeException(String.format("Section with value: %s does not exist.", incomeType)));
    }

    // find section name from OutgoingType
    private static String outgoingSection(OutgoingType outgoingType) {
        var section = fullSectionMapping
                .entrySet()
                .stream()
                .filter(sectionMap -> sectionMap.getValue().contains(outgoingType))
                .findFirst();
        return section.map(Map.Entry::getKey).orElseThrow(() -> new RuntimeException(String.format("Section with value: %s does not exist.", outgoingType)));
    }
}
