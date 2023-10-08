package uk.gov.justice.laa.crime.meansassessment.mapper;

import org.mapstruct.*;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentCriteriaDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        uses = AssessmentCriteriaEntity.class)
public interface AssessmentCriteriaEntityMapper {
    AssessmentCriteriaDTO updateAssessmentCriteriaEntityToAssessmentCriteriaDTO(final AssessmentCriteriaEntity assessment);

}