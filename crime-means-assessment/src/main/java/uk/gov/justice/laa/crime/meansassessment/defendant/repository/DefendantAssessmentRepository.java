package uk.gov.justice.laa.crime.meansassessment.defendant.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.meansassessment.defendant.entity.DefendantAssessmentEntity;

@Repository
public interface DefendantAssessmentRepository extends CrudRepository<DefendantAssessmentEntity, String> {
}
