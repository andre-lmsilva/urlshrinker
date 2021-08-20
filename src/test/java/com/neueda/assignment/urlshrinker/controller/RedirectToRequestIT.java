package com.neueda.assignment.urlshrinker.controller;

import com.neueda.assignment.urlshrinker.StandardIT;
import com.neueda.assignment.urlshrinker.math.Base62;
import com.neueda.assignment.urlshrinker.model.entity.URLEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@DisplayName("Given I request to be redirected to the original URL...")
class RedirectToRequestIT extends StandardIT {

    @Test
    @DisplayName("when the used alias does not exists, then I receive NOT_FOUND as response.")
    void withNonExistingAlias_respondsWithNotFound() throws Exception {
        ResultActions result = performGet("/{urlAlias}", "a1b2c3d4");
        result.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(is("No URL found for the short version 'a1b2c3d4'.")));
    }

    @Test
    @DisplayName("when the used alias exists, then I receive a FOUND response with Location header.")
    void withExistingAlias_respondsWithFound() throws Exception {
        URLEntry urlEntry = new URLEntry("https://www.google.com", new Date());
        urlEntry = urlEntryRepository.save(urlEntry);
        String urlAlias = Base62.encode(urlEntry.getId());

        ResultActions result = performGet("/{urlAlias}", urlAlias);

        result.andExpect(status().isFound())
            .andExpect(header().string("Location", urlEntry.getUrlAddress()));

    }

}
