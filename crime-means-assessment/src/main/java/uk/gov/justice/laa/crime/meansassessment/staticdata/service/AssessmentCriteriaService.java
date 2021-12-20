package uk.gov.justice.laa.crime.meansassessment.staticdata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This is a sample service and the required code should be moved to the MeansAssessmentService
 */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
@Service
public class AssessmentCriteriaService {

    private final AssessmentCriteriaRepository assessmentCriteriaRepository;

    public AssessmentCriteriaEntity findById(Long assessmentCriteriaId) {
        return assessmentCriteriaRepository.findById(assessmentCriteriaId).orElse(null);
    }

    public List<AssessmentCriteriaEntity> findAll() {
        List<AssessmentCriteriaEntity> results = new ArrayList<>();
        Iterable<AssessmentCriteriaEntity> queryResult = assessmentCriteriaRepository.findAll();
        if(queryResult.iterator().hasNext()) {
            results = StreamSupport.stream(queryResult.spliterator(), false).collect(Collectors.toList());
        }
        return results;
    }

    public List<AssessmentCriteriaEntity> findAssessmentCriteriaApplicableAfter(LocalDateTime dateFrom) {
        return assessmentCriteriaRepository.findByDateFromAfter(dateFrom);
    }

    public List<AssessmentCriteriaEntity> findAssessmentCriteriaApplicableBefore(LocalDateTime dateTo) {
        return assessmentCriteriaRepository.findByDateToBefore(dateTo);
    }

    public List<AssessmentCriteriaEntity> findAssessmentCriteriaApplicableBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return assessmentCriteriaRepository.findByDateFromAfterAndDateToBefore(dateFrom, dateTo);
    }
}
