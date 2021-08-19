package com.nueda.assignment.urlshrinker.repository;

import com.nueda.assignment.urlshrinker.model.entity.URLEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Specialization of {@link JpaRepository} providing capabilities to persist and retrieve {@link URLEntry} entities.
 */
public interface URLEntryRepository extends JpaRepository<URLEntry, UUID> {

    Optional<URLEntry> findByUrlAlias(String urlAlias);

    Optional<URLEntry> findByUrlAddressOrUrlAlias(String urlAddress, String urlAlias);

}
