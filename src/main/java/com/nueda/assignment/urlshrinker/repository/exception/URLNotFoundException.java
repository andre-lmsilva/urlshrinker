package com.nueda.assignment.urlshrinker.repository.exception;

/**
 * Thrown when an URL is requested by its alias, but no {@link com.nueda.assignment.urlshrinker.model.entity.URLEntry}
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
