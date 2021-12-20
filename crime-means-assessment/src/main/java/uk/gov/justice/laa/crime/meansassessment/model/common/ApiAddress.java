
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
 * The address schema
 * <p>
 * Details of an Address
 * 
 */
@Generated("jsonschema2pojo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiAddress {

    /**
     * Address ID
     * (Required)
     * 
     */
    @SerializedName("addressId")
    @Expose
    @NotNull
    public String addressId;
    /**
     * Address Line 1
     * (Required)
     * 
     */
    @SerializedName("line1")
    @Expose
    @NotNull
    public String line1;
    /**
     * Address Line 2.
     * 
     */
    @SerializedName("line2")
    @Expose
    public String line2;
    /**
     * Address Lne 3.
     * 
     */
    @SerializedName("line3")
    @Expose
    public String line3;
    /**
     * City
     * 
     */
    @SerializedName("city")
    @Expose
    public String city;
    /**
     * Post Code
     * (Required)
     * 
     */
    @SerializedName("postCode")
    @Expose
    @NotNull
    public String postCode;
    /**
     * County
     * 
     */
    @SerializedName("county")
    @Expose
    public String county;
    /**
     * Country
     * 
     */
    @SerializedName("country")
    @Expose
    public String country;

}
