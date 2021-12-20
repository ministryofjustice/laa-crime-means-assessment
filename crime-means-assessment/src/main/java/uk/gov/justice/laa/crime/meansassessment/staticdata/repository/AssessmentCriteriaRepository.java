package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssessmentCriteriaRepository extends CrudRepository<AssessmentCriteriaEntity, Long> {

    @Deprecated(forRemoval = true)
    List<AssessmentCriteriaEntity> findByDateFromAfter(LocalDateTime dateFrom);
    @Deprecated(forRemoval = true)
    List<AssessmentCriteriaEntity> findByDateToBefore(LocalDateTime dateTo);
    @Deprecated(forRemoval = true)
    List<AssessmentCriteriaEntity> findByDateFromAfterAndDateToBefore(LocalDateTime dateFrom, LocalDateTime dateTo);

    @Query("SELECT ace from AssessmentCriteriaEntity ace where ace.dateFrom <= :date and (ace.dateTo >= :date or ace.dateTo is null)")
    List<AssessmentCriteriaEntity> findAssessmentCriteriaForDate(@Param("date") LocalDateTime date);

}
