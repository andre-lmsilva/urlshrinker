package com.nueda.assignment.urlshrinker.model.request;


import com.nueda.assignment.urlshrinker.model.BeanValidationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShortenURLRequestTest extends BeanValidationTest<ShortenURLRequest> {

    @Test
    @DisplayName("A ShortenURLRequest should have an URL address different of null.")
    public void shortenURLRequest_withNullUrlAddress_failsToValidate() {
        ShortenURLRequest request = new ShortenURLRequest();
        verifyViolationForFieldWithMessage("urlAddress", "must not be blank", request);
    }

    @Test
    @DisplayName("A ShortenURLRequest should have a non-blank URL address.")
    public void shortenURLRequest_withBlackURLAddress_failsToValidate() {
        ShortenURLRequest request = new ShortenURLRequest("");
        verifyViolationForFieldWithMessage("urlAddress", "must not be blank", request);
    }

    @Test
    @DisplayName("A ShortenURLRequest should have a valid URL address.")
    public void shortenURLRequest_withNonURLAddress_failsToValidate() {
        ShortenURLRequest request = new ShortenURLRequest("invalid url");
        verifyViolationForFieldWithMessage("urlAddress", "must be a valid URL", request);
    }

    @Test
    @DisplayName("A ShortenURLRequest should have a valid URL address no longer than 2.000 characters.")
    public void shortenURLRequest_withURLAddressLongerThan2000Characters_failsToValidate() {
        String longQueryParameter = new String(new char[2000]).replace("\0", "A");
        String url = String.format("https://www.google.com?query=%s", longQueryParameter);
        ShortenURLRequest request = new ShortenURLRequest(url);

        verifyViolationForFieldWithMessage("urlAddress", "must have up to 2000 characters", request);
    }

    @Test
    @DisplayName("A ShortenURLRequest with a valid URL address is considered valid.")
    public void shortenURLRequest_withValidURLAddress_succeedsToValidate() {
        ShortenURLRequest request = new ShortenURLRequest("https://www.google.com?q=Java");
        verifyNoViolationFor(request);
    }

}