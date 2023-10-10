package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;

import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessDataAdapter.mapDetailCodeToIncomeType;
import static uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessDataAdapter.mapDetailCodeToOutgoingType;

@UtilityClass
public class MaatToStatelessDataAdapter {
    public static Map<AgeRange, Integer> childGroupingsFromChildWeightings(List<ApiAssessmentChildWeighting> childWeightings) {
        return childWeightings.stream().collect(Collectors.toMap(
                weighting -> Arrays.stream(AgeRange.values())
                        .filter(age -> age.getLowerLimit() == weighting.getLowerAgeRange() && age.getUpperLimit() == weighting.getUpperAgeRange())
                        .findFirst().get(),
                ApiAssessmentChildWeighting::getNoOfChildren));
    }

    public static List<Income> incomesFromSectionSummaries(List<ApiAssessmentSectionSummary> sectionSummaries, AssessmentCriteriaEntity assessmentCriteria) {
        return sectionSummaries.stream().map(sectionSummary ->
                sectionSummary.getAssessmentDetails().stream().map(detail -> {
                    final var incomeType = mapDetailCodeToIncomeType(getDetailCodeFromDetailId(assessmentCriteria, detail.getCriteriaDetailId()));
                    final var applicant = new FrequencyAmount(detail.getApplicantFrequency(), detail.getApplicantAmount());
                    if (detail.getPartnerAmount() != null) {
                        final var partner = new FrequencyAmount(detail.getPartnerFrequency(), detail.getPartnerAmount());
                        return new Income(incomeType, applicant, partner);
                    } else {
                        return new Income(incomeType, applicant, null);
                    }
                }).toList()
        ).reduce(Collections.emptyList(), (l1, l2) -> {
            final var newList = new ArrayList<>(l1);
            newList.addAll(l2);
            return newList;
        });
    }

    public static List<Outgoing> outgoingsFromSectionSummaries(List<ApiAssessmentSectionSummary> sectionSummaries, AssessmentCriteriaEntity assessmentCriteria) {
        return sectionSummaries.stream().map(sectionSummary ->
                sectionSummary.getAssessmentDetails().stream().map(detail -> {
                    final var outgoingType = mapDetailCodeToOutgoingType(getDetailCodeFromDetailId(assessmentCriteria, detail.getCriteriaDetailId()));
                    final var applicant = new FrequencyAmount(detail.getApplicantFrequency(), detail.getApplicantAmount());
                    if (detail.getPartnerAmount() != null) {
                        final var partner = new FrequencyAmount(detail.getPartnerFrequency(), detail.getPartnerAmount());
                        return new Outgoing(outgoingType, applicant, partner);
                    } else {
                        return new Outgoing(outgoingType, applicant, null);
                    }
                }).toList()
        ).reduce(Collections.emptyList(), (l1, l2) -> {
            final var newList = new ArrayList<>(l1);
            newList.addAll(l2);
            return newList;
        });
    }

    private static String getDetailCodeFromDetailId(AssessmentCriteriaEntity assessmentCriteria, int criteriaDetailId) {
        return assessmentCriteria.getAssessmentCriteriaDetails()
                .stream()
                .filter(criteriaDetail -> criteriaDetail.getId() == criteriaDetailId)
                .findFirst().get().getAssessmentDetail().getDetailCode();
    }
}
