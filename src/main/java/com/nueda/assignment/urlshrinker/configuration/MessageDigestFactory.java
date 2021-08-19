package com.nueda.assignment.urlshrinker.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Prepares a {@link MessageDigest} with SHA-1 algorithm as a Spring Bean ready to be injected an used.
 */
@Configuration
public class MessageDigestFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDigestFactory.class);

    @Bean("sha1MessageDigest")
    public MessageDigest prepareSha1MessageDigest() throws NoSuchAlgorithmException {
        LOGGER.info("Preparing a SHA1 message digest instance.");
        return MessageDigest.getInstance("SHA1");
    }

}
