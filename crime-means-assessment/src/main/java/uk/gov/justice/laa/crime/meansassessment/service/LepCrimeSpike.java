package uk.gov.justice.laa.crime.meansassessment.service;

import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.MeansAssessmentRequestDTO;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.NewWorkReason;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class LepCrimeSpike {
    static public String execute(Date submissionDate, int annualIncome) {
        BigDecimal total = new BigDecimal(annualIncome);
        ApiAssessmentChildWeighting child = new ApiAssessmentChildWeighting()
                .withChildWeightingId(27)
                .withNoOfChildren(1);
        var childList = Arrays.asList(child);
        AssessmentCriteriaChildWeightingEntity childWeighting = AssessmentCriteriaChildWeightingEntity
                .builder()
                .id(27)
                .weightingFactor(new BigDecimal(0.1))
                .build();
        var weightingSet = new HashSet<>(Arrays.asList(childWeighting));
        // assessmentStatus has to be set 'COMPLETE' otherwise the return value is null
        MeansAssessmentRequestDTO requestDTO = MeansAssessmentRequestDTO
                .builder()
                .childWeightings(childList)
                .assessmentStatus(CurrentStatus.COMPLETE)
                .newWorkReason(NewWorkReason.FMA)
                .build();
        AssessmentCriteriaEntity criteria = AssessmentCriteriaEntity
                .builder()
                .assessmentCriteriaChildWeightings(weightingSet)
                .applicantWeightingFactor(new BigDecimal(1.0))
                .partnerWeightingFactor(new BigDecimal(0))
                .initialLowerThreshold(new BigDecimal(34731))
                .initialUpperThreshold(new BigDecimal(46000))
                .build();
        AssessmentCriteriaChildWeightingService childWeightingService = new AssessmentCriteriaChildWeightingService();
        MeansAssessmentDTO result = new InitMeansAssessmentService(childWeightingService)
                .execute(total, requestDTO, criteria);
        return result.getInitAssessmentResult().getResult();
    }
}
