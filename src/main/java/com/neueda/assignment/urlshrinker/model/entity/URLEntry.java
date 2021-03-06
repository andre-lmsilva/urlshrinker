package com.neueda.assignment.urlshrinker.model.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

/**
 * Holds the relation between the short version of an URL and the original URL.
 */
@Entity
@Table(name = "URL_ENTRY")
@Data
@NoArgsConstructor
public class URLEntry {

    public static final int INITIAL_URL_ALIAS_LENGTH = 6;
    public static final int MAX_URL_ALIAS_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 2000, message = "must have up to {max} characters")
    @URL
    private String urlAddress;

    private Long totalViews = 0L;

    @NotNull
    @Column(updatable = false)
    private Date createdAt;

    public URLEntry(String urlAddress, Date createdAt) {
        this.urlAddress = urlAddress;
        this.createdAt = createdAt;
    }

}
