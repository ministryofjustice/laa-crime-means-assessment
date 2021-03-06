package uk.gov.justice.laa.crime.meansassessment.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MeansAssessmentRequestDTO {
    private String laaTransactionId;
    private Integer repId;
    private Integer cmuId;
    private String userId;
    private LocalDateTime initialAssessmentDate;
    private String otherBenefitNote;
    private String otherIncomeNote;
    private String employmentStatus;
    private String initAssessmentNotes;
    private CurrentStatus assessmentStatus;
    private List<ApiAssessmentSectionSummary> sectionSummaries;
    private List<ApiAssessmentChildWeighting> childWeightings;
    private Boolean hasPartner;
    private Boolean partnerContraryInterest;
    private AssessmentType assessmentType;
    private CaseType caseType;
    private ApiUserSession userSession;
    private ApiIncomeEvidenceSummary incomeEvidenceSummary;
    private ApiCrownCourtOverview crownCourtOverview;
    private MagCourtOutcome magCourtOutcome;
    // INIT specific fields
    private Integer usn;
    private ReviewType reviewType;
    private NewWorkReason newWorkReason;
    // FULL specific fields
    private LocalDateTime fullAssessmentDate;
    private String otherHousingNote;
    private BigDecimal initTotalAggregatedIncome;
    private String fullAssessmentNotes;
}
