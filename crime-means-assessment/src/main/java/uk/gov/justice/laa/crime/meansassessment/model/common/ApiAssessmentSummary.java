
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
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


/**
 * Assessment Summary
 * 
 */
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiAssessmentSummary {

    /**
     * Applicant Annual Total
     * (Required)
     * 
     */
    @SerializedName("applicantAnnualTotal")
    @Expose
    @NotNull
    public Double applicantAnnualTotal;
    /**
     * Partner Annual Total
     * 
     */
    @SerializedName("partnerAnnualTotal")
    @Expose
    public Double partnerAnnualTotal;
    /**
     * Annual Total
     * (Required)
     * 
     */
    @SerializedName("annualTotal")
    @Expose
    @NotNull
    public Double annualTotal;
    @SerializedName("assessmentDetail")
    @Expose
    @Size(min = 1)
    @Valid
    @Builder.Default
    public List<ApiAssessmentDetail> assessmentDetail = new ArrayList<ApiAssessmentDetail>();

}
