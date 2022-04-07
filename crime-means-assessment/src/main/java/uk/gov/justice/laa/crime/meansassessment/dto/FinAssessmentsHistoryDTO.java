package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FinAssessmentsHistoryDTO {
    private LocalDateTime initialAssessmentDate;
    private String assessmentType;
    private Integer cmuId;
    private Integer initialAscrId;
    private Integer fullAscrId;
    private LocalDateTime timeStamp;
    private LocalDateTime dateCreated;
    private Integer assessmentId;
    private BigDecimal adjustedLivingAllowance;
    private String fullAssessmentNotes;
    private String otherHousingNote;
    private String fullResult;
    private String fullResultReason;
    private BigDecimal totalAnnualDisposableIncome;
    private String fassInitStatus;
    private String fassFullStatus;
    private BigDecimal adjustedIncomeValue;
    private String initApplicationEmploymentStatus;
    private Boolean initAppPartner;
    private String initAssessmentNotes;
    private String otherBenefitNote;
    private String otherIncomeNote;
    private String initResult;
    private String initResultReason;
    private BigDecimal initTotalAggregatedIncome;
    private String nworCode;
    private Integer repId;
    private String userCreated;
    private BigDecimal totalAggregatedExpense;
    private LocalDateTime incomeEvidenceRecDate;
    private String residentialStatus;
    private LocalDateTime incomeEvidenceDueDate;
    private String incomeEvidenceNotes;
    private LocalDateTime incomeUpliftRemoveDate;
    private LocalDateTime incomeUpliftApplyDate;
    private Integer incomeUpliftPercentage;
    private LocalDateTime firstIncomeReminderDate;
    private LocalDateTime secondIncomeReminderDate;
    private Integer usn;
    private String valid;
    private Boolean fullAssessmentAvailable;
    private LocalDateTime fullAssessmentDate;
    private String replaced;
    private LocalDateTime dateCompleted;
    private String userModified;
    private String rtCode;
    private List<FinAssessmentDetailsHistoryDTO> assessmentDetailsList;
    private List<FinAssessmentChildWeightHistoryDTO> childWeightingsList;
}
