package com.nueda.assignment.urlshrinker.fixture;

import com.nueda.assignment.urlshrinker.model.request.ShortenURLRequest;

public class ShortenURLRequestFixture {

    public static ShortenURLRequest getDefault() {
       ShortenURLRequest request = new ShortenURLRequest();
       request.setUrlAddress("https://url.test.com");
       return  request;
    }

}
