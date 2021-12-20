
package uk.gov.justice.laa.crime.meansassessment.model.initial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentStatus;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentSummary;

import javax.annotation.processing.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Initial Means Assessment Response
 * <p>
 * The response data of the Initial Means Assessment service
 * 
 */
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiCreateMeansAssessmentResponse {

    /**
     * Unique Identifier of an Assessment.
     * (Required)
     * 
     */
    @SerializedName("assessmentId")
    @Expose
    @NotNull
    public String assessmentId;
    /**
     * Unique Identifier of the Criteria.
     * (Required)
     * 
     */
    @SerializedName("criteriaId")
    @Expose
    @NotNull
    public Integer criteriaId;
    /**
     * Time Stamp of the transaction
     * 
     */
    @SerializedName("transactionDateTime")
    @Expose
    public LocalDateTime transactionDateTime;
    /**
     * Total Aggregated income of the application.
     * (Required)
     * 
     */
    @SerializedName("totalAggregatedIncome")
    @Expose
    @NotNull
    public Double totalAggregatedIncome;
    /**
     * Adjusted Income of the Application
     * (Required)
     * 
     */
    @SerializedName("adjustedIncomeValue")
    @Expose
    @NotNull
    public Double adjustedIncomeValue;
    /**
     * Lower Threshold Value from Ref Data
     * (Required)
     * 
     */
    @SerializedName("lowerThreshold")
    @Expose
    @NotNull
    public Double lowerThreshold;
    /**
     * Upper Threshold Value from Ref Data
     * (Required)
     * 
     */
    @SerializedName("upperThreshold")
    @Expose
    @NotNull
    public Double upperThreshold;
    /**
     * Outcome of an Assessment
     * (Required)
     * 
     */
    @SerializedName("result")
    @Expose
    @NotNull
    public String result;
    /**
     * The reason for the outcome 
     * (Required)
     * 
     */
    @SerializedName("resultReason")
    @Expose
    @NotNull
    public String resultReason;
    /**
     * Assessment Status Details
     * (Required)
     * 
     */
    @SerializedName("assessmentStatus")
    @Expose
    @Valid
    @NotNull
    public ApiAssessmentStatus assessmentStatus;
    @SerializedName("assessmentSummary")
    @Expose
    @Size(min = 1)
    @Valid
    @Builder.Default
    public List<ApiAssessmentSummary> assessmentSummary = new ArrayList<ApiAssessmentSummary>();

}
