package uk.gov.justice.laa.crime.meansassessment.staticdata.enums.converter;

import org.apache.commons.lang3.StringUtils;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.HardshipReviewStatus;

import javax.persistence.AttributeConverter;

public class HardshipReviewStatusConverter  implements AttributeConverter<HardshipReviewStatus, String> {

    @Override
    public String convertToDatabaseColumn(HardshipReviewStatus attribute) {
        if (attribute == null) {return null; }
        return attribute.getStatus();
    }

    @Override
    public HardshipReviewStatus convertToEntityAttribute(String statusFromDB) {
        if (StringUtils.isBlank(statusFromDB)) { return null; }
        return HardshipReviewStatus.getFrom(statusFromDB);
    }
}
