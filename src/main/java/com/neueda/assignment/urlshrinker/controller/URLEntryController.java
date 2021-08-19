package com.neueda.assignment.urlshrinker.controller;

import com.neueda.assignment.urlshrinker.model.request.ShortenURLRequest;
import com.neueda.assignment.urlshrinker.repository.URLEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

/**
 * Handles requests to shorten and retrieve URLs.
 */
@RestController
public class URLEntryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(URLEntryController.class);
    private final URLEntryService urlEntryService;

    public URLEntryController(URLEntryService urlEntryService) {
        this.urlEntryService = urlEntryService;
    }

    /**
     * Handles requests to shorten URLs. The received URL is delegated to {@link URLEntryService#shortUrl(String)} which
     * will return the generated alias. A response is regenerated with status {@link HttpStatus#CREATED} and a map
     * in its body containing the generated alias as value of the "urlAlias" element.
     *
     * @param shortenURLRequest Request containing the URL to be shorten.
     *
     * @return {@link ResponseEntity} containing the urlAlias attribute and {@link HttpStatus#CREATED} as status code.
     */
    @PostMapping(
        path = "/api/v1/url_entry",
        consumes = { MediaType.APPLICATION_JSON_VALUE },
        produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<Map<String, Object>> shortenURL(@RequestBody @Valid ShortenURLRequest shortenURLRequest) {
        LOGGER.info("Received request to shorten URL: {}", shortenURLRequest);
        Map<String, Object> response = Map.of(
            "urlAlias", this.urlEntryService.shortUrl(shortenURLRequest.getUrlAddress())
        );
        LOGGER.info("Short version '{}' generated for URL '{}'.", response.get("urlAlias"),
            shortenURLRequest.getUrlAddress());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Looks up for the original URL address based on the received alias and, when found, responds with
     * {@link HttpStatus#FOUND} status and the original URL address set in the "Location" response header.
     *
     * @param urlAlias  URL alias to look up.
     *
     * @return {@link ResponseEntity} redirecting the user to the original URL address.
     */
    @GetMapping(path="/{urlAlias}")
    public ResponseEntity<Void> redirectTo(@PathVariable String urlAlias) {
        LOGGER.info("Request to redirect to URL received for alias '{}'.", urlAlias);
        String urlAddress = this.urlEntryService.findUrlAddressByUrlAlias(urlAlias);
        LOGGER.info("Redirecting to URL '{}'.", urlAddress);

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(urlAddress))
            .build();
    }

}
