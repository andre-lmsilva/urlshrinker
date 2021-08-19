package com.neueda.assignment.urlshrinker.repository.exception;

import com.neueda.assignment.urlshrinker.model.entity.URLEntry;

/**
 * Thrown when an URL is requested by its alias, but no {@link URLEntry}
 * is found in the application database.
 */
public class URLNotFoundException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public URLNotFoundException(String message) {
        super(message);
    }

}
