package com.neueda.assignment.urlshrinker.controller;

import com.neueda.assignment.urlshrinker.StandardIT;
import com.neueda.assignment.urlshrinker.math.Base62;
import com.neueda.assignment.urlshrinker.model.entity.URLEntry;
import com.neueda.assignment.urlshrinker.model.request.ShortenURLRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@Tag("integration")
@DisplayName("Given I submit a request to short an URL...")
class ShortenURLRequestIT extends StandardIT {

    @Test
    @DisplayName("when the request body is empty, then I receive 'Invalid request body' as response message.")
    void withEmptyRequestBody_respondsWithBadRequest() throws Exception {
        ResultActions result = performPost("/api/v1/url_entry", null);

        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(is("Invalid request body.")));
    }

    @Test
    @DisplayName("when the urlAddress field in the request is blank, then I receive 'must not be blank' as validation message for the field.")
    void withEmptyURLAddress_respondsWithBadRequest() throws Exception {
        ResultActions result = performPost("/api/v1/url_entry", new ShortenURLRequest());

        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.urlAddress").value(is("must not be blank")));
    }

    @Test
    @DisplayName("when the urlAddress is valid, then I receive the urlAlias as response.")
    void withValidURLAddress_respondsWithCreated() throws Exception {
        ResultActions resultActions = performPost(
            "/api/v1/url_entry", new ShortenURLRequest("https://www.google.com")
        );

        resultActions.andExpect(status().isCreated())
            .andExpect(jsonPath("$.urlAlias").value(matchesPattern("^[0-9a-z]*$")));
    }

    @Test
    @DisplayName("when submitting an already existing URL, receive the same urlAlias as response.")
    void whenSubmittingSameURLTwice_responseWithSameURLAlias() throws Exception {
        URLEntry urlEntry = new URLEntry("https://www.google.com", new Date());
        urlEntry = urlEntryRepository.save(urlEntry);
        String expectedAlias = Base62.encode(urlEntry.getId());

        ResultActions resultActions = performPost(
            "/api/v1/url_entry", new ShortenURLRequest("https://www.google.com")
        );

        resultActions.andExpect(status().isCreated())
            .andExpect(jsonPath("$.urlAlias").value(is(expectedAlias)));

    }

}
