package uk.gov.justice.laa.crime.meansassessment.staticdata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoggingData{

    MAAT_ID("maatId"),
    LAA_TRANSACTION_ID("laaTransactionId");

    private String value;
}
