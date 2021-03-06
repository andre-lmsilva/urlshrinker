package com.neueda.assignment.urlshrinker.controller;

import com.neueda.assignment.urlshrinker.fixture.ShortenURLRequestFixture;
import com.neueda.assignment.urlshrinker.model.request.ShortenURLRequest;
import com.neueda.assignment.urlshrinker.repository.URLEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class URLEntryControllerTest {

    private static final String FAKE_ALIAS = "fkAlias";

    @Mock
    private URLEntryService urlEntryService;

    private URLEntryController urlEntryController;

    @BeforeEach
    public void setUp() {
        this.urlEntryController = new URLEntryController(this.urlEntryService, "http://localhost:8080");
    }

    @Test
    @DisplayName("Given I submit a valid URL address to be shorten, then I should receive an URL alias in response.")
    void shortenUrl_withValidURLAddress_returnsGeneratedURLAlias() {
        ShortenURLRequest request = ShortenURLRequestFixture.getDefault();
        doReturn(FAKE_ALIAS).when(this.urlEntryService).shortUrl(request.getUrlAddress());

        ResponseEntity<Map<String, Object>> result = this.urlEntryController.shortenURL(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<String, Object> responseBody = result.getBody();
        assertThat(responseBody).containsEntry("urlAlias", "http://localhost:8080/"+FAKE_ALIAS);
    }

    @Test
    @DisplayName("Given I request to be redirect to the original URL using a short alias, then I should receive a redirect response.")
    void redirectTo_withValidURLAlias_responseWithRedirectionToOriginalURL() {
        String originalURLAddress = "https://www.google.com";
        doReturn(originalURLAddress).when(this.urlEntryService).findUrlAddressByUrlAlias(FAKE_ALIAS);

        ResponseEntity<Void> result = this.urlEntryController.redirectTo(FAKE_ALIAS);

        assertThat(result).hasFieldOrPropertyWithValue("status", HttpStatus.FOUND);
        assertThat(result.getHeaders().getLocation()).isEqualTo(URI.create(originalURLAddress));
    }

}