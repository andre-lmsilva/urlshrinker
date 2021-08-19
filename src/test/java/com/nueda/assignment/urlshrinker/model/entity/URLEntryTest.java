package com.nueda.assignment.urlshrinker.model.entity;


import com.nueda.assignment.urlshrinker.fixture.URLEntryFixture;
import com.nueda.assignment.urlshrinker.model.BeanValidationTest;
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
        public void urlAddressAsNull_failsToValidate() {
            URLEntry entry = new URLEntry(null, null, null);
            verifyViolationForFieldWithMessage("urlAddress", "must not be blank", entry);
        }

        @Test
        @DisplayName("not be blank.")
        public void urlAddressAsBlank_failsToValidate() {
            URLEntry entry = new URLEntry("", null, null);
            verifyViolationForFieldWithMessage("urlAddress", "must not be blank", entry);
        }

        @Test
        @DisplayName("not be invalid.")
        public void urlAddressWithInvalidURL_failsToValidate() {
            URLEntry entry = new URLEntry("invalid url", null, null);
            verifyViolationForFieldWithMessage("urlAddress", "must be a valid URL", entry);
        }

        @Test
        @DisplayName("not be longer than 2.000 characters.r")
        public void urlAddressWithMoreThant2000Characters_failsToValidate() {
            String longQueryParameter = new String(new char[2000]).replace("\0", "A");
            String url = String.format("https://www.google.com?query=%s", longQueryParameter);
            URLEntry entry = new URLEntry(url, null, null);
            verifyViolationForFieldWithMessage("urlAddress", "must have up to 2000 characters",
                entry);
        }

        @Test
        @DisplayName("succeed to validate when valid.")
        public void urlAddressWithValidURL_succeedsToValidate() {
            URLEntry entry = new URLEntry("https://www.google.com?query=Java", "fkAlias", new Date());
            verifyNoViolationForField("urlAddress", entry);
        }

    }

    @Nested
    @DisplayName("urlAlias should...")
    class URLAliasTest {

        @Test
        @DisplayName("not be null.")
        public void urlAliasAsNull_failsToValidate() {
            URLEntry entry = new URLEntry(null, null, null);
            verifyViolationForFieldWithMessage("urlAlias", "must not be blank", entry);
        }

        @Test
        @DisplayName("not be blank.")
        public void urlAliasAsBlank_failsToValidate() {
            URLEntry entry = new URLEntry(null, "", null);
            verifyViolationForFieldWithMessage("urlAlias", "must not be blank", entry);
        }

        @Test
        @DisplayName("not be shorter than 6 characters.")
        public void urlAliasShorterThanSixCharacters_failsToValidate() {
            URLEntry entry = new URLEntry(null, "abcde", null);
            verifyViolationForFieldWithMessage("urlAlias", "size must be between 6 and 10",
                entry);
        }

        @Test
        @DisplayName("not be longer than 10 characters.")
        public void urlAliasLongerThanTenCharacters_failsToValidate() {
            URLEntry entry = new URLEntry(null, "abcdefghijklm", null);
            verifyViolationForFieldWithMessage("urlAlias", "size must be between 6 and 10",
                entry);
        }

        @Test
        @DisplayName("only have lowercase letters and numbers.")
        public void urlAliasWithInvalidFormat_failsToValidate() {
            URLEntry entry = new URLEntry(null, "abc_def", null);
            verifyViolationForFieldWithMessage("urlAlias",
                "must only have numbers and lower case letters", entry);

        }

        @Test
        @DisplayName("be considered valid with a value composed by numbers and lower case letter and length between 6 and 10 characters.")
        public void urlAliasWithValidAlias_succeedsToValidate() {
            URLEntry entry = new URLEntry(null, "a1b2c3d4", null);
            verifyNoViolationForField("urlAlias", entry);
        }

    }

    @Nested
    @DisplayName("createdAt should...")
    class CreatedAtTest {

        @Test
        @DisplayName("not be null.")
        public void createdAtAsNull_failsToValidate() {
             URLEntry entry = new URLEntry(null, null, null);
             verifyViolationForFieldWithMessage("createdAt", "must not be null", entry);
        }

        @Test
        @DisplayName("be considered valid with any date.")
        public void createAtWithValidNull_succeedsToValidate() {
            URLEntry entry = new URLEntry(null, null, new Date());
            verifyNoViolationForField("createdAt", entry);
        }

    }

    @Test
    @DisplayName("An URLEntry with all required fields correctly filled in should succeeds to validate.")
    public void urlEntry_withAllRequiredFieldsCorrectlyFilledInd_succeedsToValidate() {
        URLEntry entry = URLEntryFixture.getDefault();
        verifyNoViolationFor(entry);
    }

}
