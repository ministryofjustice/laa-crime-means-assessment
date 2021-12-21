
package uk.gov.justice.laa.crime.meansassessment.model.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;
import javax.validation.constraints.NotNull;


/**
 * Assessment Status Details
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated("jsonschema2pojo")
public class ApiAssessmentStatus {

    /**
     * Assessment Status
     * <p>
     * 
     * (Required)
     * 
     */
    @SerializedName("status")
    @Expose
    @NotNull
    public String status;
    /**
     * The description
     * <p>
     * 
     * 
     */
    @SerializedName("description")
    @Expose
    public String description;
    /**
     * Indicates whether an Assessment is completed or not
     * 
     */
    @SerializedName("complete")
    @Expose
    public Boolean complete;
    /**
     * Indicates whether an Assessment is in progress or not
     * 
     */
    @SerializedName("inProgress")
    @Expose
    public Boolean inProgress;

}
