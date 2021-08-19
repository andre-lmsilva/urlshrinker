package com.neueda.assignment.urlshrinker.model.request;

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
public class ShortenURLRequest {

    @NotBlank
    @Size(max = 2000, message = "must have up to {max} characters")
    @URL
    private String urlAddress;

}
