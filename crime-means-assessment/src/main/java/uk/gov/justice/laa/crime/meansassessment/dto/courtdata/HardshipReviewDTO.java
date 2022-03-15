package uk.gov.justice.laa.crime.meansassessment.dto.courtdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardshipReviewDTO {

    private Integer id;
    private Integer cmuId;
    private String notes;
    private String decisionNotes;
    private LocalDateTime reviewDate;
    private String reviewResult;
    private BigDecimal disposableIncome;
    private BigDecimal disposableIncomeAfterHardship;
    private NewWorkReason newWorkReason;
    private SolicitorCosts solicitorCosts;
    private HardshipReviewStatus status;
    private String courtType;

}
