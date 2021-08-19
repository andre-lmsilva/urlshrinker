package com.neueda.assignment.urlshrinker.repository.exception;

/**
 * Thrown when the application is not cable to generate an unique URL alias for a received URL.
 */
public class NoURLAliasAvailableException extends RuntimeException {

    /**
     * {@inheritDoc}
     */
    public NoURLAliasAvailableException(String message) {
        super(message);
    }

}
