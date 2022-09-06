package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.*;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDetails;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Section;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class MeansAssessmentSectionSummaryBuilder {

    public List<AssessmentSectionSummaryDTO> buildAssessmentSectionSummary(List<AssessmentDTO> assessmentDTOList) {

        List<AssessmentSectionSummaryDTO> assessmentSectionSummaryList = new ArrayList<>();
        Arrays.stream(Section.values()).forEach(section -> {
            List<AssessmentDTO> assessmentDTOS = assessmentDTOList.stream().filter(e -> e.getSection().equals(section.name()))
                    .collect(Collectors.toList());
            if (!assessmentDTOS.isEmpty()) {
                AssessmentSectionSummaryDTO assessmentSectionSummary = getAssessmentSectionSummaryDTO(section.name(), assessmentDTOS);
                if (section.equals(Section.INITA) || section.equals(Section.INITB)) {
                    assessmentSectionSummary.setAssessmentType(AssessmentType.INIT);
                } else if (section.equals(Section.FULLA) || section.equals(Section.FULLB)) {
                    assessmentSectionSummary.setAssessmentType(AssessmentType.FULL);
                }
                assessmentSectionSummaryList.add(assessmentSectionSummary);
            }
        });
        return assessmentSectionSummaryList;
    }


    private AssessmentSectionSummaryDTO getAssessmentSectionSummaryDTO(String section, List<AssessmentDTO> assessmentDTOS) {
        AssessmentSectionSummaryDTO assessmentSectionSummary = new AssessmentSectionSummaryDTO();
        assessmentSectionSummary.setSection(section);
        BigDecimal applicantTotalAmt = BigDecimal.ZERO;
        BigDecimal partnerTotalAmt = BigDecimal.ZERO;
        for (AssessmentDTO assessmentDTO : assessmentDTOS) {
            assessmentSectionSummary.getAssessmentDetails().add(getAssessmentDetail(assessmentDTO));
            applicantTotalAmt = applicantTotalAmt.add(getAssessmentSectionSummaryTotal(assessmentDTO.getApplicantAmount(), assessmentDTO.getApplicantFrequency()));
            partnerTotalAmt = partnerTotalAmt.add(getAssessmentSectionSummaryTotal(assessmentDTO.getPartnerAmount(), assessmentDTO.getPartnerFrequency()));
        }
        assessmentSectionSummary.setApplicantAnnualTotal(applicantTotalAmt);
        assessmentSectionSummary.setPartnerAnnualTotal(partnerTotalAmt);
        assessmentSectionSummary.setAnnualTotal(assessmentSectionSummary.getApplicantAnnualTotal().add(assessmentSectionSummary.getPartnerAnnualTotal()));
        return assessmentSectionSummary;
    }


    protected AssessmentDetailDTO getAssessmentDetail(AssessmentDTO assessmentDTO) {
        AssessmentDetailDTO assessmentDetailDTO = new AssessmentDetailDTO();
        assessmentDetailDTO.setCriteriaDetailId(assessmentDTO.getCriteriaDetailId());
        assessmentDetailDTO.setApplicantAmount(assessmentDTO.getApplicantAmount());
        assessmentDetailDTO.setPartnerAmount(assessmentDTO.getPartnerAmount());
        assessmentDetailDTO.setAssessmentDescription(assessmentDTO.getCriteriaDetailDescription());
        assessmentDetailDTO.setAssessmentDetailCode(assessmentDTO.getAssessmentDetailCode());
        assessmentDetailDTO.setDateModified(assessmentDTO.getDateModified());
        assessmentDetailDTO.setApplicantFrequency(assessmentDTO.getApplicantFrequency());
        assessmentDetailDTO.setPartnerFrequency(assessmentDTO.getPartnerFrequency());

        return assessmentDetailDTO;
    }

    protected BigDecimal getAssessmentSectionSummaryTotal(BigDecimal assessmentAmt, Frequency frequency) {

        BigDecimal detailTotal = BigDecimal.ZERO;
        if (assessmentAmt != null && !BigDecimal.ZERO.equals(assessmentAmt) && frequency != null) {
            detailTotal = assessmentAmt.multiply(BigDecimal.valueOf(frequency.getWeighting()));
        }
        return detailTotal;

    }

    public AssessmentDTO buildAssessmentDTO(AssessmentCriteriaDetailEntity assessmentCriteriaDetailEntity,
                                            FinancialAssessmentDetails financialAssessmentDetails) {

        AssessmentDTO assessmentDTO = new AssessmentDTO();
        assessmentDTO.setCriteriaDetailId(financialAssessmentDetails.getCriteriaDetailId());
        assessmentDTO.setSection(assessmentCriteriaDetailEntity.getSection());
        assessmentDTO.setCriteriaDetailDescription(assessmentCriteriaDetailEntity.getDescription());
        assessmentDTO.setFinancialDetailId(assessmentCriteriaDetailEntity.getId());
        assessmentDTO.setApplicantAmount(financialAssessmentDetails.getApplicantAmount());
        assessmentDTO.setApplicantFrequency(financialAssessmentDetails.getApplicantFrequency());
        assessmentDTO.setPartnerFrequency(financialAssessmentDetails.getPartnerFrequency());
        assessmentDTO.setPartnerAmount(financialAssessmentDetails.getPartnerAmount());
        assessmentDTO.setDateModified(financialAssessmentDetails.getDateModified());
        assessmentDTO.setSequence(assessmentCriteriaDetailEntity.getSeq());

        if (null != assessmentCriteriaDetailEntity.getAssessmentDetail()) {
            assessmentDTO.setAssessmentDetailCode(assessmentCriteriaDetailEntity.getAssessmentDetail().getDetailCode());
        }

        return assessmentDTO;
    }
}
