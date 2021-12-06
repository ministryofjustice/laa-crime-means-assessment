package uk.gov.justice.laa.crime.meansassessment.enums.converter;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.meansassessment.enums.Frequency;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA to Enum converter class. It is used when persisting Frequency enum to DB and when loading Frequency enum from DB
 */
@Converter(autoApply = true)
public class FrequencyConverter implements AttributeConverter<Frequency, String> {

    @Override
    public String convertToDatabaseColumn(Frequency frequency) {
        if (frequency == null) {return null; }
        return frequency.getCode();
    }

    @Override
    public Frequency convertToEntityAttribute(String codeFromDB) {
        if (StringUtils.isBlank(codeFromDB)) { return null; }
        return Frequency.getFrom(codeFromDB);
    }
}
