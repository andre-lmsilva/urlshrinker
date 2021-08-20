package com.neueda.assignment.urlshrinker.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Value object holding the URL requested the be shortened by the application.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Request object to shorten an URL address.")
public class ShortenURLRequest {

    @NotBlank
    @Size(max = 2000, message = "must have up to {max} characters")
    @URL
    @ApiModelProperty(notes = "URL address to be shortened.")
    private String urlAddress;

}
