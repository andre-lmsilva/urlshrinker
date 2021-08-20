package com.neueda.assignment.urlshrinker.repository;

import com.neueda.assignment.urlshrinker.model.entity.URLEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Specialization of {@link JpaRepository} providing capabilities to persist and retrieve {@link URLEntry} entities.
 */
public interface URLEntryRepository extends JpaRepository<URLEntry, Long> {

    Optional<URLEntry> findByUrlAddress(String urlAddress);

}
