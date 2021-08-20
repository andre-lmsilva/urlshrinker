package com.neueda.assignment.urlshrinker.fixture;

import com.neueda.assignment.urlshrinker.model.entity.URLEntry;

import java.util.Date;

public class URLEntryFixture {

    public static URLEntry getDefault() {
        URLEntry urlEntry = new URLEntry();
        urlEntry.setId(9999L);
        urlEntry.setUrlAddress("http://url.test.com");
        urlEntry.setCreatedAt(new Date());
        return urlEntry;
    }

    public static URLEntry getGoogleHomeEntry() {
        URLEntry urlEntry = getDefault();
        urlEntry.setUrlAddress("https://www.google.com");
        return urlEntry;
    }

}