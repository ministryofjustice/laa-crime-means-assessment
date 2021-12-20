
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
 * The supplierInfo schema
 * <p>
 * An explanation about the purpose of this instance.
 * 
 */
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiSupplierInfo {

    /**
     * Account Number
     * (Required)
     * 
     */
    @SerializedName("accountNumber")
    @Expose
    @NotNull
    public Integer accountNumber;
    /**
     * Name of the supplier
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    @NotNull
    public String name;
    /**
     * The address schema
     * <p>
     * Details of an Address
     * (Required)
     * 
     */
    @SerializedName("address")
    @Expose
    @Valid
    @NotNull
    public ApiAddress address;

}
