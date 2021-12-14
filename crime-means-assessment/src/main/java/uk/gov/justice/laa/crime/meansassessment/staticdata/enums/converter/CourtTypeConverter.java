package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CourtType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA to Enum converter class. It is used when persisting CourtType enum to DB and when loading CourtType enum from DB
 */
@Converter(autoApply = true)
public class CourtTypeConverter implements AttributeConverter<CourtType, String> {
    @Override
    public String convertToDatabaseColumn(CourtType courtType) {
        if (courtType == null) { return null; }
        return courtType.getCourtType();
    }

    @Override
    public CourtType convertToEntityAttribute(String courtTypeFromDB) {
        if (StringUtils.isBlank(courtTypeFromDB)) { return null; }
        return CourtType.getFrom(courtTypeFromDB);
    }
}
