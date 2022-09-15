package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.IncomeEvidenceEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailRepository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.IncomeEvidenceRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class IncomeEvidenceService {

    private final IncomeEvidenceRepository incomeEvidenceRepository;

    public Optional<IncomeEvidenceEntity> getIncomeEvidenceById(String id) {
        return incomeEvidenceRepository.findById(id);
    }
}
