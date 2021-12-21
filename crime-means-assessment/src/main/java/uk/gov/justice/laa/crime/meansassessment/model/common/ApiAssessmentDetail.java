
package uk.gov.justice.laa.crime.meansassessment.model.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Assessment details
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated("jsonschema2pojo")
public class ApiAssessmentDetail {

    /**
     * Criteria Id
     * (Required)
     * 
     */
    @SerializedName("criteriaDetailsId")
    @Expose
    @NotNull
    public String criteriaDetailsId;
    /**
     * An explanation about the purpose of this instance.
     * (Required)
     * 
     */
    @SerializedName("applicantAmount")
    @Expose
    @NotNull
    public Double applicantAmount;
    /**
     * An explanation about the purpose of this instance.
     * 
     */
    @SerializedName("partnerAmount")
    @Expose
    public Double partnerAmount;
    /**
     * The Frequency
     * (Required)
     * 
     */
    @SerializedName("applicantFrequency")
    @Expose
    @Valid
    @NotNull
    public ApiFrequency applicantFrequency;
    /**
     * The Frequency
     * 
     */
    @SerializedName("partnerFrequency")
    @Expose
    @Valid
    public ApiFrequency partnerFrequency;
    /**
     * An explanation about the purpose of this instance.
     * 
     */
    @SerializedName("description")
    @Expose
    public String description;

}
