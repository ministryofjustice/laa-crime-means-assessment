package uk.gov.justice.laa.crime.meansassessment.dto.maatcourtdata;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PassportAssessmentDTO extends Assessment {

    private Integer id;
    private Integer financialAssessmentId;
    private Integer repId;
    private String nworCode;
    private LocalDateTime dateCreated;
    private String userCreated;
    private Integer cmuId;
    private LocalDateTime assessmentDate;
    private String partnerBenefitClaimed;
    private String partnerFirstName;
    private String partnerSurname;
    private String partnerOtherNames;
    private String partnerNiNumber;
    private LocalDateTime partnerDob;
    private String incomeSupport;
    private String jobSeekers;
    private String statePensionCredit;
    private String under18FullEducation;
    private String under16;
    private String pcobConfirmation;
    private String result;
    private LocalDateTime dateModified;
    private String userModified;
    private String dwpResult;
    private String passportNote;
    private String between16And17;
    private String under18HeardInYouthCourt;
    private String under18HeardInMagsCourt;
    private LocalDateTime lastSignOnDate;
    private String esa;
    private String pastStatus;
    private String replaced;
    private LocalDateTime passportEvidenceDueDate;
    private LocalDateTime allPassportEvidenceReceivedDate;
    private Integer passportUpliftPercentage;
    private LocalDateTime passportUpliftApplyDate;
    private LocalDateTime passportUpliftRemoveDate;
    private String passportEvidenceNotes;
    private LocalDateTime firstPassportReminderDate;
    private LocalDateTime secondPassportReminderDate;
    private String valid;
    private LocalDateTime dateCompleted;
    private Integer usn;
    private String whoDWPChecked;
    private String rtCode;
}

