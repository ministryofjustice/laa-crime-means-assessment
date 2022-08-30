package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.meansassessment.dto.*;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDetails;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaDetailService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Section;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class MeansAssessmentBuilder {

    private  AssessmentCriteriaDetailService assessmentCriteriaDetailService;

    public void build(final FinancialAssessmentDTO financialAssessmentDTO, AssessmentCriteriaDetailService assessmentCriteriaDetailService) {
        this.assessmentCriteriaDetailService = assessmentCriteriaDetailService;
        List<AssessmentDTO> assessmentDTOList = getAssessmentDTO(financialAssessmentDTO.getAssessmentDetails());
        if (assessmentDTOList.size() > 0) {
            transformToAssessmentSectionSummary(assessmentDTOList, financialAssessmentDTO);
        }
    }

    private List<AssessmentDTO> getAssessmentDTO(List<FinancialAssessmentDetails> financialAssessmentDetailsList) {

        List<AssessmentDTO> assessmentDTOList = new ArrayList<AssessmentDTO>();
        financialAssessmentDetailsList.forEach(e -> {
            Optional<AssessmentCriteriaDetailEntity> assessmentCriteriaDetailEntity = assessmentCriteriaDetailService.getAssessmentCriteriaDetailById(e.getCriteriaDetailId());
            if (assessmentCriteriaDetailEntity.isPresent()) {
                AssessmentDTO assessmentDTO = new AssessmentDTO();
                assessmentDTO.setFinancialDetailId(assessmentCriteriaDetailEntity.get().getId());
                assessmentDTO.setSection(assessmentCriteriaDetailEntity.get().getSection());
                assessmentDTO.setCriteriaDetailDescription(assessmentCriteriaDetailEntity.get().getDescription());
                assessmentDTO.setFinancialDetailId(e.getId());
                assessmentDTO.setApplicantAmount(e.getApplicantAmount());
                assessmentDTO.setApplicantFrequency(e.getApplicantFrequency());
                assessmentDTO.setPartnerFrequency(e.getPartnerFrequency());
                assessmentDTO.setPartnerAmount(e.getPartnerAmount());
                assessmentDTO.setDateModified(LocalDateTime.now());
                assessmentDTO.setSequence(assessmentCriteriaDetailEntity.get().getSeq());
                assessmentDTOList.add(assessmentDTO);
            }
        });

        if (assessmentDTOList.size() > 0) {
            Collections.sort(assessmentDTOList, Comparator.comparing(AssessmentDTO::getSection)
                    .thenComparing(AssessmentDTO::getSequence));
        }

        return assessmentDTOList;
    }

    private void transformToAssessmentSectionSummary(List<AssessmentDTO> assessmentDTOList, FinancialAssessmentDTO financialAssessmentDTO) {

        Arrays.stream(Section.values()).forEach(section -> {
            List<AssessmentDTO> assessmentDTOS = assessmentDTOList.stream().filter(e -> e.getSection().equals(section.name()))
                    .collect(Collectors.toList());
            if (!assessmentDTOS.isEmpty() && assessmentDTOS.size() > 0) {
                AssessmentSectionSummaryDTO assessmentSectionSummary = getAssessmentSectionSummaryDTO(section.name(), assessmentDTOS);
                if (section.equals(Section.INITA) || section.equals(Section.INITB)) {
                    InitialAssessmentDTO initialAssessmentDTO = new InitialAssessmentDTO();
                    initialAssessmentDTO.getAssessmentSectionSummaries().add(assessmentSectionSummary);
                    financialAssessmentDTO.getInitialAssessment().add(initialAssessmentDTO);
                } else if (section.equals(Section.FULLA) || section.equals(Section.FULLB)) {
                    FullAssessmentDTO fullAssessmentDTO = new FullAssessmentDTO();
                    fullAssessmentDTO.getAssessmentSectionSummaries().add(assessmentSectionSummary);
                    financialAssessmentDTO.getFullAssessment().add(fullAssessmentDTO);
                }
            }
        });
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

    private AssessmentDetailDTO getAssessmentDetail(AssessmentDTO assessmentDTO) {
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

    private BigDecimal getAssessmentSectionSummaryTotal(BigDecimal assessmentAmt, Frequency frequency) {

        BigDecimal detailTotal = BigDecimal.ZERO;
        if (assessmentAmt != null && !BigDecimal.ZERO.equals(assessmentAmt)) {
            if (frequency != null) {
                detailTotal = assessmentAmt.multiply(BigDecimal.valueOf(frequency.getWeighting()));
            }
        }
        return detailTotal;

    }
}
