package com.neueda.assignment.urlshrinker.repository;

import com.neueda.assignment.urlshrinker.math.Base62;
import com.neueda.assignment.urlshrinker.model.entity.URLEntry;
import com.neueda.assignment.urlshrinker.repository.exception.URLNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Standard implementation of {@link URLEntry}. This implementation creates the URL alias (shorter version of the URL)
 * encoding the sequential primary key assigned when a new URLEntry is created. The encoding/decoding tasks is delegated
 * to {@link Base62} and its methods.
 */
@Service
public class StandardURLEntryService implements URLEntryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardURLEntryService.class);

    private final URLEntryRepository urlEntryRepository;

    public StandardURLEntryService(URLEntryRepository urlEntryRepository) {
        this.urlEntryRepository = urlEntryRepository;
    }

    /**
     * Looks up for an {@link URLEntry} with the received address. When no entry is found, the
     * {@link #create(String)} method is invoked to persist a new entry and its result is returned. If an entry
     * is found, it is returned immediately.
     *
     * @param urlAddress    URL to look up or create if not existing yet.
     *
     * @return An {@link URLEntry} containing the associating between the alias and the received URL.
     */
    protected URLEntry findOrCreate(String urlAddress) {
        LOGGER.debug("Looking up for entries with URL address '{}'.", urlAddress);
        Optional<URLEntry> urlEntry = this.urlEntryRepository.findByUrlAddress(urlAddress);
        return urlEntry.orElseGet(() -> this.create(urlAddress));
    }

    /**
     * Creates and persists a new {@link URLEntry} entity.
     *
     * @param urlAddress    URL address of the entry.
     *
     * @return Newly created and persisted {@link URLEntry}.
     */
    protected URLEntry create(String urlAddress) {
        LOGGER.info("No shorten version found for URL '{}'. Creating a new one.", urlAddress);
        URLEntry newURLEntry = new URLEntry(urlAddress, new Date());
        return this.urlEntryRepository.save(newURLEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String shortUrl(String urlAddress) {
        LOGGER.info("Shortening URL '{}'.", urlAddress);
        return Base62.encode(
            this.findOrCreate(urlAddress).getId()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findUrlAddressByUrlAlias(String urlAlias) {
        LOGGER.info("Looking up for URL assigned to alias '{}'.", urlAlias);
        Long urlEntryId = Base62.decode(urlAlias);
        return this.urlEntryRepository.findById(urlEntryId)
            .map(URLEntry::getUrlAddress)
            .orElseThrow(() -> new URLNotFoundException(
                String.format("No URL found for the short version '%s'.", urlAlias)
            ));
    }

}
