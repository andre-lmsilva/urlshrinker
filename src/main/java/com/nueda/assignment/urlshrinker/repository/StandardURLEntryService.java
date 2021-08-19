package com.nueda.assignment.urlshrinker.repository;

import com.nueda.assignment.urlshrinker.model.entity.URLEntry;
import com.nueda.assignment.urlshrinker.repository.exception.NoURLAliasAvailableException;
import com.nueda.assignment.urlshrinker.repository.exception.URLNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Optional;

/**
 * Standard implementation of {@link URLEntry}. This implementation is based on URL alias extracted from the beginning
 * of URL address SHA-1 hashes, similar to what is done by git for commit hashes, enhanced with a fallback algorithm to
 * extend its precision up to {@link URLEntry#MAX_URL_ALIAS_LENGTH}, which represents an universe of permutations up to
 * 922.393.263.052.800, with the drawback that the shortening process increases in complexity along the time.
 */
@Service
public class StandardURLEntryService implements URLEntryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardURLEntryService.class);

    private final URLEntryRepository urlEntryRepository;
    private final MessageDigest sha1MessageDigest;

    public StandardURLEntryService(URLEntryRepository urlEntryRepository,
                                   @Qualifier("sha1MessageDigest") MessageDigest sha1MessageDigest) {
        this.urlEntryRepository = urlEntryRepository;
        this.sha1MessageDigest = sha1MessageDigest;
    }

    /**
     * Generates an alias for the received URL address. The alias is obtained from the first n characters (received in
     * the hashSize argument) of the SHA-1 hash of the received URL address.
     *
     * @param urlAddress    URL to have its alias generated.
     * @param hashSize      Length of the resulting alias, took from the first n characters of the resulting SHA-1 hash.
     *
     * @return First n characters of the URL address SHA-1 resulting hash.
     */
    public String generateUrlAliasForUrlAddress(String urlAddress, int hashSize) {
        LOGGER.debug("Generating URL alias with {} characters for address '{}'.", urlAddress, hashSize);
        this.sha1MessageDigest.reset();
        this.sha1MessageDigest.update(urlAddress.getBytes(StandardCharsets.UTF_8));
        String fullHash = String.format("%040x", new BigInteger(1, this.sha1MessageDigest.digest()));
        return fullHash.substring(0, hashSize);
    }

    /**
     * Looks up for an {@link URLEntry} with the received address or its alias. When no entry is found, the
     * {@link #create(String, String)} method is invoked to persist a new entry and its result is returned. If an entry
     * is found matching the URL address or its alias, the {@link #handleExistingEntry(String, int, URLEntry)} method is
     * invoked and its result is returned. The URL alias is generated invoking the
     * {@link #generateUrlAliasForUrlAddress(String, int)} method.
     *
     * @param urlAddress    URL to look up or create if not existing yet.
     * @param hashSize      Length of the alias to be generated.
     *
     * @return An {@link URLEntry} containing the associating between the alias and the received URL.
     */
    protected URLEntry findOrCreate(String urlAddress, int hashSize) {
        String urlAlias = this.generateUrlAliasForUrlAddress(urlAddress, hashSize);

        LOGGER.debug("Looking up for entries with URL address '{}' or URL alias '{}'.", urlAddress, urlAlias);
        Optional<URLEntry> urlEntry = this.urlEntryRepository.findByUrlAddressOrUrlAlias(urlAddress, urlAlias);
        return urlEntry.map((URLEntry entry) -> this.handleExistingEntry(urlAddress, hashSize, entry))
            .orElseGet(() -> this.create(urlAddress, urlAlias));
    }

    /**
     * Creates and persists a new {@link URLEntry} entity.
     *
     * @param urlAddress    URL address of the entry.
     * @param urlAlias      URL alias of the entry.
     *
     * @return Newly created and persisted {@link URLEntry}.
     */
    protected URLEntry create(String urlAddress, String urlAlias) {
        LOGGER.info("No shorten version found for URL '{}'. Creating a new one with URL alias '{}.'", urlAddress, urlAlias);
        URLEntry newURLEntry = new URLEntry(urlAddress, urlAlias, new Date());
        return this.urlEntryRepository.save(newURLEntry);
    }

    /**
     * Handle possible existing entries for the received URL, offering a fallback alternative for clashing of aliases.
     * When the existing entry has the URL address equals to the received URL address, nothing is done and the entry
     * is returned immediately. However, when the existing entry has a different URL address than the received one
     * (indicating an URL alias clashing), the method delegates to the {@link #findOrCreate(String, int)} method to
     * try to find or create the entry, incrementing the length of the URL alias by 1 (one) up to a maximum of
     * {@link URLEntry#MAX_URL_ALIAS_LENGTH}. When this limit is reached, a {@link NoURLAliasAvailableException} is
     * thrown by this method.
     *
     * @param receivedUrlAddress    URL being shorten.
     * @param hashSize              Length of the URL alias being generated.
     * @param existingEntry         {@link URLEntry} found when looking up by URL address or URL alias.
     *
     * @return {@link URLEntry} matching the received URL address.
     */
    protected URLEntry handleExistingEntry(String receivedUrlAddress, int hashSize, URLEntry existingEntry) {
        if (existingEntry.getUrlAddress().equals(receivedUrlAddress)) {
            LOGGER.info("URL found for URL alias '{}'.", existingEntry.getUrlAlias());
            LOGGER.debug("URL details: {}", existingEntry);
            return existingEntry;

        } else if (hashSize < URLEntry.MAX_URL_ALIAS_LENGTH) {
            LOGGER.info("URL alias clash found. Trying to generate an unique alias with {} characters.", hashSize + 1);
            LOGGER.debug("URL entry details: {}", existingEntry);
            return this.findOrCreate(receivedUrlAddress, hashSize + 1);

        } else {
            LOGGER.warn("It was not possible to generate an unique URL alis for URL '%s'. The permutations are most " +
                "likely exhausted.");
            throw new NoURLAliasAvailableException(
                String.format(
                    "It was not possible to generate an unique URL alias for URL '%s' up to '%s' characters.",
                    receivedUrlAddress, URLEntry.MAX_URL_ALIAS_LENGTH
                )
            );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String shortUrl(String urlAddress) {
        LOGGER.info("Shortening URL '{}'.", urlAddress);
        return this.findOrCreate(urlAddress, URLEntry.INITIAL_URL_ALIAS_LENGTH).getUrlAlias();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findUrlAddressByUrlAlias(String urlAlias) {
        LOGGER.info("Looking up for URL assigned to alias '{}'.", urlAlias);
        return this.urlEntryRepository.findByUrlAlias(urlAlias).map(URLEntry::getUrlAddress)
            .orElseThrow(() -> new URLNotFoundException(
                String.format("No URL found for the short version '%s'.", urlAlias)
            ));
    }

}
