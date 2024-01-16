package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.gov.justice.laa.crime.enums.CaseType;

@Converter(autoApply = true)
public class CaseTypeConverter implements AttributeConverter<CaseType, String> {

    @Override
    public String convertToDatabaseColumn(CaseType caseType) {
        if (caseType == null) {
            return null;
        }
        return caseType.getCaseType();
    }

    @Override
    public CaseType convertToEntityAttribute(String caseTypeFromDB) {
        if (StringUtils.isBlank(caseTypeFromDB)) {
            return null;
        }
        return CaseType.getFrom(caseTypeFromDB);
    }
}
