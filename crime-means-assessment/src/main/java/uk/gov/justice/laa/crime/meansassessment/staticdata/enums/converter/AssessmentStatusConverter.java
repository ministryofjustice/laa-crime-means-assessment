package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentStatus;

import javax.persistence.AttributeConverter;

public class AssessmentStatusConverter implements AttributeConverter<AssessmentStatus, String> {

    @Override
    public String convertToDatabaseColumn(AssessmentStatus attribute) {
        if (attribute == null) {return null; }
        return attribute.getStatus();
    }

    @Override
    public AssessmentStatus convertToEntityAttribute(String statusFromDB) {
        if (StringUtils.isBlank(statusFromDB)) { return null; }
        return AssessmentStatus.getFrom(statusFromDB);
    }
}
