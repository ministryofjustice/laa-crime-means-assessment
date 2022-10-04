package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentDetail;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaChildWeightingRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailFrequencyRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.CaseTypeAssessmentCriteriaDetailValueRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentCriteriaService {

    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final AssessmentCriteriaDetailFrequencyRepository assessmentCriteriaDetailFrequencyRepository;
    private final CaseTypeAssessmentCriteriaDetailValueRepository caseTypeAssessmentCriteriaDetailValueRepository;
    private final AssessmentCriteriaChildWeightingRepository assessmentCriteriaChildWeightingRepository;

    AssessmentCriteriaEntity getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) {
        log.info("Retrieving assessment criteria for date: {}", assessmentDate);
        AssessmentCriteriaEntity assessmentCriteriaForDate = assessmentCriteriaRepository.findAssessmentCriteriaForDate(assessmentDate);
        if (assessmentCriteriaForDate != null) {
            // If there is no partner or there is a partner with contrary interest, set partnerWeightingFactor to null
            if (!hasPartner || contraryInterest) {
                assessmentCriteriaForDate.setPartnerWeightingFactor(BigDecimal.ZERO);
            }
            return assessmentCriteriaForDate;
        } else {
            log.error("No Assessment Criteria found for date {}", assessmentDate);
            throw new AssessmentCriteriaNotFoundException(String.format("No Assessment Criteria found for date %s", assessmentDate));
        }
    }

    void checkCriteriaDetailFrequency(AssessmentCriteriaDetailEntity criteriaDetail, Frequency frequency) {
        Optional<AssessmentCriteriaDetailFrequencyEntity> detailFrequency =
                assessmentCriteriaDetailFrequencyRepository.findByAssessmentCriteriaDetailAndFrequency(criteriaDetail, frequency);
        if (detailFrequency.isEmpty()) {
            throw new ValidationException(String.format("Frequency: %s not valid for: %s", frequency.getCode(), criteriaDetail.getDescription()));
        }
    }

    void checkAssessmentDetail(CaseType caseType, String section, AssessmentCriteriaEntity assessmentCriteria, ApiAssessmentDetail detail) {
        AssessmentCriteriaDetailEntity criteriaDetail =
                assessmentCriteria.getAssessmentCriteriaDetails().stream().filter(
                        d -> d.getSection().equals(section) && d.getId().equals(detail.getCriteriaDetailId())
                ).findFirst().orElseThrow(
                        () -> new ValidationException(
                                String.format("Section: %s criteria detail item: %d does not exist for criteria id: %s",
                                        section, detail.getCriteriaDetailId(), assessmentCriteria.getId()))
                );

        Frequency applicantFrequency = detail.getApplicantFrequency();
        if (applicantFrequency != null) {
            checkCriteriaDetailFrequency(criteriaDetail, applicantFrequency);
        }

        Frequency partnerFrequency = detail.getPartnerFrequency();
        if (partnerFrequency != null) {
            checkCriteriaDetailFrequency(criteriaDetail, partnerFrequency);
        }

        CaseTypeAssessmentCriteriaDetailValueEntity criteriaDetailValue =
                caseTypeAssessmentCriteriaDetailValueRepository.findByAssessmentCriteriaDetailAndCaseType(
                        criteriaDetail, caseType
                ).orElse(null);

        if (criteriaDetailValue != null &&
                ((criteriaDetailValue.getApplicantValue().compareTo(detail.getApplicantAmount()) != 0 ||
                        (applicantFrequency != null &&
                                !applicantFrequency.getCode().equals(criteriaDetailValue.getApplicantFrequency().getCode())
                        )) ||
                        (criteriaDetailValue.getPartnerValue().compareTo(detail.getPartnerAmount()) != 0 ||
                                (partnerFrequency != null &&
                                        !partnerFrequency.getCode().equals(criteriaDetailValue.getPartnerFrequency().getCode())
                                )
                        ))) {
            throw new ValidationException("Incorrect amount entered for: " + criteriaDetail.getDescription());
        }
    }

    public Optional<AssessmentCriteriaChildWeightingEntity> getAssessmentCriteriaChildWeightingsById(Integer id) {
        return assessmentCriteriaChildWeightingRepository.findById(id);
    }

    public Optional<AssessmentCriteriaEntity> getAssessmentCriteriaById(Integer id) {
        return assessmentCriteriaRepository.findById(id);
    }

}
