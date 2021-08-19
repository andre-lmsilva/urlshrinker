package com.nueda.assignment.urlshrinker.repository;

import com.nueda.assignment.urlshrinker.fixture.URLEntryFixture;
import com.nueda.assignment.urlshrinker.model.entity.URLEntry;
import com.nueda.assignment.urlshrinker.repository.exception.NoURLAliasAvailableException;
import com.nueda.assignment.urlshrinker.repository.exception.URLNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StandardURLEntryServiceTest {

    // Expected SHA-1 hash: 928e4eb916836b6dd76522f3d3d5157c99838056
    private static final String FAKE_URL = "http://url.test.com";
    private static final String FAKE_ALIAS = "fkAlias";

    @Mock
    private URLEntryRepository urlEntryRepository;

    private MessageDigest sha1MessageDigest;

    private StandardURLEntryService urlEntryService;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        this.sha1MessageDigest = MessageDigest.getInstance("SHA1");
        this.urlEntryService = spy(new StandardURLEntryService(this.urlEntryRepository, this.sha1MessageDigest));
    }

    @Nested
    @DisplayName("Given I generate an alias for an URL...")
    class GenerateUrlAliasForUrlAddressTest {

        @Test
        @DisplayName("with any URL address value and hash size as 6, than it returns first 6 characters of resulting hash.")
        public void withAnyUrlAddressValueAndHashSizeAsSix_returnsFirstSizeCharactersOfResultingHash() {
            String result = urlEntryService.generateUrlAliasForUrlAddress(FAKE_URL, 6);

            assertThat(result).isEqualTo("928e4e");
        }

    }

    @Nested
    @DisplayName("Given I receive an URL to shorten...")
    class FindOrCreateTest {

        @Test
        @DisplayName("with a non-existing URL, then delegates to the create() method to persist a new URL entry and returns it.")
        public void withNonExistingUrl_delegatesToCreateMethodAndReturnsThePersistedEntry() {
            doReturn(FAKE_ALIAS).when(urlEntryService).generateUrlAliasForUrlAddress(FAKE_URL, 7);
            doReturn(Optional.empty()).when(urlEntryRepository).findByUrlAddressOrUrlAlias(FAKE_URL, FAKE_ALIAS);
            URLEntry persistedURLEntry = URLEntryFixture.getDefault();
            doReturn(persistedURLEntry).when(urlEntryService).create(FAKE_URL, FAKE_ALIAS);

            URLEntry result = urlEntryService.findOrCreate(FAKE_URL, 7);

            assertThat(result).isSameAs(persistedURLEntry);
            verify(urlEntryService, never()).handleExistingEntry(anyString(), anyInt(), any(URLEntry.class));
        }

        @Test
        @DisplayName("with an existing URL, then delegates to the handleExistingEntry() method and returns the found entry.")
        public void withExistingUrl_delegatesToHandleExistingEntryAndReturnsTheFoundEntry() {
            doReturn(FAKE_ALIAS).when(urlEntryService).generateUrlAliasForUrlAddress(FAKE_URL, 7);
            URLEntry existingEntry = URLEntryFixture.getDefault();
            doReturn(Optional.of(existingEntry)).when(urlEntryRepository).findByUrlAddressOrUrlAlias(FAKE_URL, FAKE_ALIAS);
            doReturn(existingEntry).when(urlEntryService).handleExistingEntry(FAKE_URL, 7, existingEntry);

            URLEntry result = urlEntryService.findOrCreate(FAKE_URL, 7);

            assertThat(result).isSameAs(existingEntry);
            verify(urlEntryService, never()).create(anyString(), anyString());
        }

    }

    @Nested
    @DisplayName("Given I create a new URLEntry...")
    class CreateTest {

        @Test
        @DisplayName("with valid URL and alias, then creates, persists and returns a new URLEntry entity.")
        public void withValidURLAndAlias_createsPersistsAndReturnsNewURLEntry() {
            URLEntry persistedEntry = URLEntryFixture.getDefault();
            doReturn(persistedEntry).when(urlEntryRepository).save(any(URLEntry.class));

            URLEntry result = urlEntryService.create(FAKE_URL, FAKE_ALIAS);

            assertThat(result).isSameAs(persistedEntry);

            ArgumentCaptor<URLEntry> newEntryCaptor = ArgumentCaptor.forClass(URLEntry.class);
            verify(urlEntryRepository).save(newEntryCaptor.capture());
            assertThat(newEntryCaptor.getValue())
                .hasNoNullFieldsOrPropertiesExcept("id")
                .hasFieldOrPropertyWithValue("urlAddress", FAKE_URL)
                .hasFieldOrPropertyWithValue("urlAlias", FAKE_ALIAS);
        }

    }

    @Nested
    @DisplayName("Given I have to handle an existing entry...")
    class HandleExistingEntryTest {

        @Test
        @DisplayName("with existing URL address equals to the received URL address, then returns the existing entry.")
        public void withExistingURLAddressEqualsReceivedAddress_returnsTheExistingEntry() {
            URLEntry existingEntry = URLEntryFixture.getDefault();

            URLEntry result = urlEntryService.handleExistingEntry(
                existingEntry.getUrlAddress(), 7, existingEntry
            );

            assertThat(result).isSameAs(existingEntry);
            verify(urlEntryService, never()).findOrCreate(anyString(), anyInt());
        }

        @Test
        @DisplayName("with existing URL different from received URL and hash size smaller than 10, delegates to findOrCreate() method.")
        public void withExistingURLAddressDifferentAndHashSizeSmallerThanNine_delegatesToFindOrCreateMethod() {
            URLEntry existingEntry = URLEntryFixture.getDefault();
            URLEntry googleHomeEntry = URLEntryFixture.getGoogleHomeEntry();
            doReturn(googleHomeEntry).when(urlEntryService).findOrCreate(googleHomeEntry.getUrlAddress(), 8);

            URLEntry result = urlEntryService.handleExistingEntry(googleHomeEntry.getUrlAddress(), 7, existingEntry);

            assertThat(result).isSameAs(googleHomeEntry);
        }

        @Test
        @DisplayName("with existing URL different from received URL and hash size equals to 10, then throws NoURLAliasAvailableException.")
        public void withExistingURLAddressDifferentAndHashSizeEqualsToNine_throwsNoURLAliasAvailableException() {
            URLEntry existingEntry = URLEntryFixture.getGoogleHomeEntry();
            assertThatThrownBy(() -> urlEntryService.handleExistingEntry(FAKE_URL, 10, existingEntry))
                .isInstanceOf(NoURLAliasAvailableException.class)
                .hasMessage(
                    String.format(
                        "It was not possible to generate an unique URL alias for URL '%s' up to '10' characters.",
                        FAKE_URL
                    )
                );
        }

    }

    @Nested
    @DisplayName("Given I receive an URL address to shorten...")
    class ShortUrlTest {

        @Test
        @DisplayName("with no other pre-condition, then delegates to findAndCreate() method and returns the URL alias of its result.")
        public void withNoOtherPreCondition_delegatesToFindOrCreateMethodToGenerateShortenedVersionAndReturnsTheAliasOfItsResult() {
            URLEntry existingEntry = URLEntryFixture.getDefault();
            doReturn(existingEntry).when(urlEntryService).findOrCreate(FAKE_URL, URLEntry.INITIAL_URL_ALIAS_LENGTH);

            String result = urlEntryService.shortUrl(FAKE_URL);

            assertThat(result).isEqualTo(existingEntry.getUrlAlias());
        }

    }

    @Nested
    @DisplayName("Given I look up for an URL based on its URL alias...")
    class FindUrlAddressByUrlAliasTest {

        @Test
        @DisplayName("for an existing URL entry, then returns its URL address.")
        public void withExistingURLEntry_returnsUrlEntryUrlAddress() {
            URLEntry existingEntry = URLEntryFixture.getDefault();
            doReturn(Optional.of(existingEntry)).when(urlEntryRepository).findByUrlAlias(FAKE_ALIAS);

            String result = urlEntryService.findUrlAddressByUrlAlias(FAKE_ALIAS);

            assertThat(result).isEqualTo(existingEntry.getUrlAddress());
        }

        @Test
        @DisplayName("for a non-existing URL entry, then throws a URLNotFoundException.")
        public void withNonExistingURLEntry_throwsURLNotFoundException() {
            doReturn(Optional.empty()).when(urlEntryRepository).findByUrlAlias(FAKE_ALIAS);
            assertThatThrownBy(() -> urlEntryService.findUrlAddressByUrlAlias(FAKE_ALIAS))
                .isInstanceOf(URLNotFoundException.class)
                .hasMessage(String.format("No URL found for the short version '%s'.", FAKE_ALIAS));
        }

    }

}