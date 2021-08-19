package com.neueda.assignment.urlshrinker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main application class. Contains the entry point method for the entire application.
 */
@SpringBootApplication
@EnableJpaRepositories
public class Application {

    /**
     * Application entry point.
     * @param args Not used for the application.
     */
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

}
