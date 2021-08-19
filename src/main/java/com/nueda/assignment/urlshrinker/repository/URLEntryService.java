package com.nueda.assignment.urlshrinker.repository;

import com.nueda.assignment.urlshrinker.model.entity.URLEntry;
import com.nueda.assignment.urlshrinker.repository.exception.URLNotFoundException;

/**
 * Provides a high level access to all features related with shortening and retrieval of URL.
 */
public interface URLEntryService {
    /**
     * Generates a short version for the received URL.
     *
     * @param urlAddress URL to be shortened.
     * @return Shorter URL alias for the received URL address.
     */
    String shortUrl(String urlAddress);

    /**
     * Looks up for an existing {@link URLEntry} with the received URL alias. If no entry is found, a
     * {@link URLNotFoundException} is thrown.
     *
     * @param urlAlias URL alias to look up.
     * @return URL address assigned to the received URL alias
     */
    String findUrlAddressByUrlAlias(String urlAlias);
}
