package com.neueda.assignment.urlshrinker.model.entity;


import com.neueda.assignment.urlshrinker.fixture.URLEntryFixture;
import com.neueda.assignment.urlshrinker.model.BeanValidationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

class URLEntryTest extends BeanValidationTest<URLEntry> {

    @Nested
    @DisplayName("urlAddress should...")
    class URLAddressTest {

        @Test
        @DisplayName("not be null.")
        void urlAddressAsNull_failsToValidate() {
            URLEntry entry = new URLEntry(null, null);
            verifyViolationForFieldWithMessage("urlAddress", "must not be blank", entry);
        }

        @Test
        @DisplayName("not be blank.")
        void urlAddressAsBlank_failsToValidate() {
            URLEntry entry = new URLEntry("", null);
            verifyViolationForFieldWithMessage("urlAddress", "must not be blank", entry);
        }

        @Test
        @DisplayName("not be invalid.")
        void urlAddressWithInvalidURL_failsToValidate() {
            URLEntry entry = new URLEntry("invalid url", null);
            verifyViolationForFieldWithMessage("urlAddress", "must be a valid URL", entry);
        }

        @Test
        @DisplayName("not be longer than 2.000 characters.r")
        void urlAddressWithMoreThant2000Characters_failsToValidate() {
            String longQueryParameter = new String(new char[2000]).replace("\0", "A");
            String url = String.format("https://www.google.com?query=%s", longQueryParameter);
            URLEntry entry = new URLEntry(url, null);
            verifyViolationForFieldWithMessage("urlAddress", "must have up to 2000 characters",
                entry);
        }

        @Test
        @DisplayName("succeed to validate when valid.")
        void urlAddressWithValidURL_succeedsToValidate() {
            URLEntry entry = new URLEntry("https://www.google.com?query=Java", new Date());
            verifyNoViolationForField("urlAddress", entry);
        }

    }

    @Nested
    @DisplayName("createdAt should...")
    class CreatedAtTest {

        @Test
        @DisplayName("not be null.")
        void createdAtAsNull_failsToValidate() {
             URLEntry entry = new URLEntry(null, null);
             verifyViolationForFieldWithMessage("createdAt", "must not be null", entry);
        }

        @Test
        @DisplayName("be considered valid with any date.")
        void createAtWithValidNull_succeedsToValidate() {
            URLEntry entry = new URLEntry(null, new Date());
            verifyNoViolationForField("createdAt", entry);
        }

    }

    @Test
    @DisplayName("An URLEntry with all required fields correctly filled in should succeeds to validate.")
    void urlEntry_withAllRequiredFieldsCorrectlyFilledInd_succeedsToValidate() {
        URLEntry entry = URLEntryFixture.getDefault();
        verifyNoViolationFor(entry);
    }

}
