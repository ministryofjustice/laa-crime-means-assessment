package uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitorCosts {
    private BigDecimal solicitorRate;
    private BigDecimal solicitorHours;
    private BigDecimal solicitorVat;
    private BigDecimal solicitorDisb;
    private BigDecimal solicitorEstTotalCost;
}
