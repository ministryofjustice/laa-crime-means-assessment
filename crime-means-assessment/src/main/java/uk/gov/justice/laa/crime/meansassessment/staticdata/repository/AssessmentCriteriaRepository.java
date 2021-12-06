package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssessmentCriteriaRepository extends CrudRepository<AssessmentCriteriaEntity, Long> {

    List<AssessmentCriteriaEntity> findByDateFromAfter(LocalDateTime dateFrom);
    List<AssessmentCriteriaEntity> findByDateToBefore(LocalDateTime dateTo);
    List<AssessmentCriteriaEntity> findByDateFromAfterAndDateToBefore(LocalDateTime dateFrom, LocalDateTime dateTo);
}
