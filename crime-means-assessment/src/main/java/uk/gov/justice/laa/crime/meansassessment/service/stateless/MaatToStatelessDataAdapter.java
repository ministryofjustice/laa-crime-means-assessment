package uk.gov.justice.laa.crime.meansassessment.service.stateless;

import lombok.experimental.UtilityClass;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSectionSummary;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.stateless.AgeRange;

import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessDataAdapter.mapDetailCodeToIncomeType;
import static uk.gov.justice.laa.crime.meansassessment.service.stateless.StatelessDataAdapter.mapDetailCodeToOutgoingType;

@UtilityClass
public class MaatToStatelessDataAdapter {
    private static class AgeRangeNotFoundException extends RuntimeException {
        AgeRangeNotFoundException(ApiAssessmentChildWeighting weighting) {
            super(String.format("Age range with lower %d and upper %d does not exist.", weighting.getLowerAgeRange(), weighting.getUpperAgeRange()));
        }
    }

    private static class CriteriaDetailsNotFoundException extends RuntimeException {
        CriteriaDetailsNotFoundException(int criteriaDetailId) {
            super(String.format("Criteria detail id %d does not exist.", criteriaDetailId));
        }
    }

    public static Map<AgeRange, Integer> childGroupingsFromChildWeightings(List<ApiAssessmentChildWeighting> childWeightings) {
        return childWeightings.stream()
                .collect(Collectors.toMap(
                        weighting -> {
                            var w = Arrays.stream(AgeRange.values())
                                    .filter(age -> age.getLowerLimit() == weighting.getLowerAgeRange()
                                                && age.getUpperLimit() == weighting.getUpperAgeRange())
                                    .findFirst();
                            if (w.isPresent()) {
                                return w.get();
                            } else {
                                throw new AgeRangeNotFoundException(weighting);
                            }
                            },
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
        final var code = assessmentCriteria.getAssessmentCriteriaDetails()
                .stream()
                .filter(criteriaDetail -> criteriaDetail.getId() == criteriaDetailId)
                .findFirst().map(AssessmentCriteriaDetailEntity::getAssessmentDetail);
        return code.map(AssessmentDetailEntity::getDetailCode).orElseThrow(() ->
                new CriteriaDetailsNotFoundException(criteriaDetailId));
    }
}
