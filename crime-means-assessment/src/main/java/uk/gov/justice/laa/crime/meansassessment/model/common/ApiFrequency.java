
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
 * The Frequency
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated("jsonschema2pojo")
public class ApiFrequency {

    /**
     * This wll have the frequency code of the selection
     * (Required)
     * 
     */
    @SerializedName("code")
    @Expose
    @NotNull
    public String code;
    /**
     * Frequency 
     * 
     */
    @SerializedName("description")
    @Expose
    public String description;

}
