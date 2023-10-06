package uk.gov.justice.laa.crime.meansassessment.mapper;

import org.mapstruct.*;
import uk.gov.justice.laa.crime.meansassessment.dto.AssessmentCriteriaDTO;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring",
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        builder = @Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AssessmentCriteriaEntityMapper {

    AssessmentCriteriaDTO updateAssessmentCriteriaEntityToAssessmentCriteriaDTO(final AssessmentCriteriaEntity assessment);


}