package uk.gov.justice.laa.crime.meansassessment.builder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.meansassessment.dto.*;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata.FinancialAssessmentDetails;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class MeansAssessmentSectionSummaryBuilder {


    public void buildInitialAssessment(ApiGetMeansAssessmentResponse assessmentResponse, FinancialAssessmentDTO financialAssessmentDTO,
                                       List<ApiAssessmentSectionSummary> assessmentSectionSummaryList,
                                       Optional<AssessmentCriteriaEntity> criteriaEntity) {

        ApiInitialMeansAssessment initialMeansAssessment = assessmentResponse.getInitialAssessment();
        initialMeansAssessment.setId(financialAssessmentDTO.getInitialAscrId());
        initialMeansAssessment.setAssessmentDate(financialAssessmentDTO.getInitialAssessmentDate());
        initialMeansAssessment.setOtherBenefitNote(financialAssessmentDTO.getInitOtherBenefitNote());
        initialMeansAssessment.setOtherIncomeNote(financialAssessmentDTO.getInitOtherIncomeNote());
        initialMeansAssessment.setTotalAggregatedIncome(financialAssessmentDTO.getInitTotAggregatedIncome());
        initialMeansAssessment.setAdjustedIncomeValue(financialAssessmentDTO.getInitAdjustedIncomeValue());
        initialMeansAssessment.setNotes(financialAssessmentDTO.getInitNotes());
        if (criteriaEntity.isPresent()) {
            initialMeansAssessment.setLowerThreshold(criteriaEntity.get().getInitialLowerThreshold());
            initialMeansAssessment.setUpperThreshold(criteriaEntity.get().getInitialUpperThreshold());
        }
        initialMeansAssessment.setResult(financialAssessmentDTO.getInitResult());
        initialMeansAssessment.setResultReason(financialAssessmentDTO.getInitResultReason());

        ApiAssessmentStatus assessmentStatus = new ApiAssessmentStatus();
        if (StringUtils.isNotBlank(financialAssessmentDTO.getFassInitStatus())) {
            assessmentStatus.setStatus(financialAssessmentDTO.getFassInitStatus());
            assessmentStatus.setDescription(CurrentStatus.getFrom(financialAssessmentDTO.getFassInitStatus()).getDescription());
            initialMeansAssessment.setAssessmentStatus(assessmentStatus);
        }

        ApiNewWorkReason newWorkReason = new ApiNewWorkReason();
        if (StringUtils.isNotBlank(financialAssessmentDTO.getNewWorkReason())) {
            newWorkReason.setCode(financialAssessmentDTO.getNewWorkReason());
            newWorkReason.setDescription(NewWorkReason.getFrom(financialAssessmentDTO.getNewWorkReason()).getDescription());
            newWorkReason.setType(NewWorkReason.getFrom(financialAssessmentDTO.getNewWorkReason()).getType());
            initialMeansAssessment.setNewWorkReason(newWorkReason);
        }

        ApiReviewType apiReviewType = new ApiReviewType();
        if (StringUtils.isNotBlank(financialAssessmentDTO.getRtCode())) {
            apiReviewType.setCode(financialAssessmentDTO.getRtCode());
            apiReviewType.setDescription(ReviewType.getFrom(financialAssessmentDTO.getRtCode()).getDescription());
            initialMeansAssessment.setReviewType(apiReviewType);
        }

        List<ApiAssessmentSectionSummary> initAssessmentSectionSummaries = assessmentSectionSummaryList.stream()
                .filter(e -> e.getAssessmentType().equals(AssessmentType.INIT)).collect(Collectors.toList());

        initialMeansAssessment.setAssessmentSectionSummary(initAssessmentSectionSummaries);
        assessmentResponse.setInitialAssessment(initialMeansAssessment);

    }

    public void buildFullAssessment(ApiGetMeansAssessmentResponse assessmentResponse, FinancialAssessmentDTO financialAssessmentDTO,
                                       List<ApiAssessmentSectionSummary> assessmentSectionSummaryList,
                                    Optional<AssessmentCriteriaEntity> criteriaEntity) {

        ApiFullMeansAssessment apiFullMeansAssessment = assessmentResponse.getFullAssessment();
        apiFullMeansAssessment.setId(financialAssessmentDTO.getFullAscrId());
        apiFullMeansAssessment.setAssessmentDate(financialAssessmentDTO.getFullAssessmentDate());
        apiFullMeansAssessment.setAssessmentNotes(financialAssessmentDTO.getFullAssessmentNotes());
        apiFullMeansAssessment.setAdjustedLivingAllowance(financialAssessmentDTO.getFullAdjustedLivingAllowance());
        apiFullMeansAssessment.setOtherHousingNote(financialAssessmentDTO.getFullOtherHousingNote());
        apiFullMeansAssessment.setTotalAggregatedExpense(financialAssessmentDTO.getFullTotalAggregatedExpenses());
        apiFullMeansAssessment.setTotalAnnualDisposableIncome(financialAssessmentDTO.getFullTotalAnnualDisposableIncome());
        if (criteriaEntity.isPresent()) {
            apiFullMeansAssessment.setThreshold(criteriaEntity.get().getFullThreshold());
        }
        apiFullMeansAssessment.setResult(financialAssessmentDTO.getFullResult());
        apiFullMeansAssessment.setResultReason(financialAssessmentDTO.getFullResultReason());

        assessmentResponse.setFullAvailable(Boolean.FALSE);
        if (null != financialAssessmentDTO.getFullAssessmentDate()) {
            assessmentResponse.setFullAvailable(Boolean.TRUE);
        }


        ApiAssessmentStatus assessmentStatus = new ApiAssessmentStatus();
        if (StringUtils.isNotBlank(financialAssessmentDTO.getFassFullStatus())) {
            assessmentStatus.setStatus(financialAssessmentDTO.getFassFullStatus());
            assessmentStatus.setDescription(CurrentStatus.getFrom(financialAssessmentDTO.getFassInitStatus()).getDescription());
            apiFullMeansAssessment.setAssessmentStatus(assessmentStatus);
        }


        List<ApiAssessmentSectionSummary> fullAssessmentSectionSummaries = assessmentSectionSummaryList.stream()
                .filter(e -> e.getAssessmentType().equals(AssessmentType.FULL)).collect(Collectors.toList());

        apiFullMeansAssessment.setAssessmentSectionSummary(fullAssessmentSectionSummaries);
        assessmentResponse.setFullAssessment(apiFullMeansAssessment);

    }


    public List<ApiAssessmentSectionSummary> buildAssessmentSectionSummary(List<AssessmentDTO> assessmentDTOList) {

        List<ApiAssessmentSectionSummary> assessmentSectionSummaryList = new ArrayList<>();
        Arrays.stream(Section.values()).forEach(section -> {
            List<AssessmentDTO> assessmentDTOS = assessmentDTOList.stream().filter(e -> e.getSection().equals(section.name()))
                    .collect(Collectors.toList());
            if (!assessmentDTOS.isEmpty()) {
                ApiAssessmentSectionSummary assessmentSectionSummary = getAssessmentSectionSummary(section.name(), assessmentDTOS);
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


    private ApiAssessmentSectionSummary getAssessmentSectionSummary(String section, List<AssessmentDTO> assessmentDTOS) {
        ApiAssessmentSectionSummary assessmentSectionSummary = new ApiAssessmentSectionSummary();
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


    protected ApiAssessmentDetail getAssessmentDetail(AssessmentDTO assessmentDTO) {
        ApiAssessmentDetail assessmentDetail = new ApiAssessmentDetail();
        assessmentDetail.setCriteriaDetailId(assessmentDTO.getCriteriaDetailId());
        assessmentDetail.setApplicantAmount(assessmentDTO.getApplicantAmount());
        assessmentDetail.setPartnerAmount(assessmentDTO.getPartnerAmount());
        assessmentDetail.setAssessmentDescription(assessmentDTO.getCriteriaDetailDescription());
        assessmentDetail.setAssessmentDetailCode(assessmentDTO.getAssessmentDetailCode());
        assessmentDetail.setDateModified(assessmentDTO.getDateModified());
        assessmentDetail.setApplicantFrequency(assessmentDTO.getApplicantFrequency());
        assessmentDetail.setPartnerFrequency(assessmentDTO.getPartnerFrequency());

        return assessmentDetail;
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
