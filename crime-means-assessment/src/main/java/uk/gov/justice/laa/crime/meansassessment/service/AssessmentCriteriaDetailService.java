package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaDetailRepository;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentCriteriaDetailService {

    private final AssessmentCriteriaDetailRepository assessmentCriteriaDetailRepository;

    public Optional<AssessmentCriteriaDetailEntity> getAssessmentCriteriaDetailById(Integer id) {
        return assessmentCriteriaDetailRepository.findById(id);
    }
}
