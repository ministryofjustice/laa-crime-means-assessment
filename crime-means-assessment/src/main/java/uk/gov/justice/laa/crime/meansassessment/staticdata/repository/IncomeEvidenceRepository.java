package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.IncomeEvidenceEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeEvidenceRepository extends CrudRepository<IncomeEvidenceEntity, String> {}
