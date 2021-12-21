
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
 * The newWorkReason schema
 * <p>
 * An explanation about the purpose of this instance.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated("jsonschema2pojo")
public class ApiNewWorkReason {

    /**
     * New Work reason code
     * (Required)
     * 
     */
    @SerializedName("code")
    @Expose
    @NotNull
    public String code;
    /**
     * New work reason description
     * 
     */
    @SerializedName("description")
    @Expose
    public String description;

}
