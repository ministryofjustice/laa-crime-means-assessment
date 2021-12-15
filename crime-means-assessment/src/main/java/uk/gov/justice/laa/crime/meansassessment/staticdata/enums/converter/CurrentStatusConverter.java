package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CurrentStatus;

import javax.persistence.AttributeConverter;

public class CurrentStatusConverter implements AttributeConverter<CurrentStatus, String> {

    @Override
    public String convertToDatabaseColumn(CurrentStatus attribute) {
        if (attribute == null) {return null; }
        return attribute.getStatus();
    }

    @Override
    public CurrentStatus convertToEntityAttribute(String statusFromDB) {
        if (StringUtils.isBlank(statusFromDB)) { return null; }
        return CurrentStatus.getFrom(statusFromDB);
    }
}
