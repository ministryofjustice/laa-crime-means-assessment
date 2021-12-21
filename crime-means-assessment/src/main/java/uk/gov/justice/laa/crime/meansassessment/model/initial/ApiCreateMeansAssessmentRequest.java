
package uk.gov.justice.laa.crime.meansassessment.model.initial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;

import javax.annotation.processing.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Initial Means Assessment Request
 * <p>
 * Data contract for the initial means assessment request
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated("jsonschema2pojo")
public class ApiCreateMeansAssessmentRequest {

    /**
     * The txn / correlation UUID
     * (Required)
     * 
     */
    @SerializedName("laaTransactionId")
    @Expose
    @NotNull
    public String laaTransactionId;
    /**
     * MAAT / Rep Id
     * (Required)
     * 
     */
    @SerializedName("repId")
    @Expose
    @NotNull
    public Integer repId;
    /**
     * Case Management Unit Id
     * (Required)
     * 
     */
    @SerializedName("cmuId")
    @Expose
    @NotNull
    public Integer cmuId;
    /**
     * User  ID
     * (Required)
     * 
     */
    @SerializedName("userId")
    @Expose
    @NotNull
    public String userId;
    /**
     * Time Stamp of the transaction
     * (Required)
     * 
     */
    @SerializedName("transactionDateTime")
    @Expose
    @NotNull
    public LocalDateTime transactionDateTime;
    /**
     * Effective Date of the assessment
     * (Required)
     * 
     */
    @SerializedName("assessmentDate")
    @Expose
    @NotNull
    public LocalDateTime assessmentDate;
    /**
     * Benefit Note
     * 
     */
    @SerializedName("otherBenefitNote")
    @Expose
    public String otherBenefitNote;
    /**
     * Income Note.
     * 
     */
    @SerializedName("otherIncomeNote")
    @Expose
    public String otherIncomeNote;
    /**
     * Employment status code
     * 
     */
    @SerializedName("employmentStatus")
    @Expose
    public String employmentStatus;
    /**
     * Notes field.
     * 
     */
    @SerializedName("notes")
    @Expose
    public String notes;
    /**
     * Assessment Status Details
     * 
     */
    @SerializedName("assessmentStatus")
    @Expose
    @Valid
    public ApiAssessmentStatus assessmentStatus;
    /**
     * The newWorkReason schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @SerializedName("newWorkReason")
    @Expose
    @Valid
    @NotNull
    public ApiNewWorkReason newWorkReason;
    /**
     * An explanation about the purpose of this instance.
     * 
     */
    @SerializedName("reviewType")
    @Expose
    @Valid
    public ApiReviewType reviewType;
    /**
     * The supplierInfo schema
     * <p>
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @SerializedName("supplierInfo")
    @Expose
    @Valid
    @NotNull
    public ApiSupplierInfo supplierInfo;
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("assessmentSummary")
    @Expose
    @Size(min = 1)
    @Valid
    @NotNull
    @Builder.Default
    public List<ApiAssessmentSummary> assessmentSummary = new ArrayList<ApiAssessmentSummary>();
    /**
     * Indicates whether the applicant has a partner (used to check if partner weighting factor is applicable)
     * (Required)
     * 
     */
    @SerializedName("hasPartner")
    @Expose
    @NotNull
    public Boolean hasPartner;
    /**
     * Indicates whether the applicant's partner has a contrary interest' (used to check if partner weighting factor is applicable)
     * (Required)
     * 
     */
    @SerializedName("partnerContraryInterest")
    @Expose
    @NotNull
    public Boolean partnerContraryInterest;

}
