package com.neueda.assignment.urlshrinker.repository;

import com.neueda.assignment.urlshrinker.fixture.URLEntryFixture;
import com.neueda.assignment.urlshrinker.math.Base62;
import com.neueda.assignment.urlshrinker.model.entity.URLEntry;
import com.neueda.assignment.urlshrinker.repository.exception.URLNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StandardURLEntryServiceTest {

    private static final String FAKE_URL = "http://url.test.com";
    private static final String FAKE_ALIAS = "fkAlias";

    @Mock
    private URLEntryRepository urlEntryRepository;

    @InjectMocks
    @Spy
    private StandardURLEntryService urlEntryService;

    @Nested
    @DisplayName("Given I receive an URL to shorten...")
    class FindOrCreateTest {

        @Test
        @DisplayName("with a non-existing URL, then the create() method is invoked to persist a new URL entry and returns it.")
        void withNonExistingUrl_delegatesToCreateMethodAndReturnsThePersistedEntry() {
            doReturn(Optional.empty()).when(urlEntryRepository).findByUrlAddress(FAKE_URL);
            URLEntry persistedURLEntry = URLEntryFixture.getDefault();
            doReturn(persistedURLEntry).when(urlEntryService).create(FAKE_URL);

            URLEntry result = urlEntryService.findOrCreate(FAKE_URL);

            assertThat(result).isSameAs(persistedURLEntry);
        }

        @Test
        @DisplayName("with an existing URL, then the URLEntry found is returned..")
        void withExistingUrl_delegatesToHandleExistingEntryAndReturnsTheFoundEntry() {
            URLEntry existingEntry = URLEntryFixture.getDefault();
            doReturn(Optional.of(existingEntry)).when(urlEntryRepository).findByUrlAddress(FAKE_URL);

            URLEntry result = urlEntryService.findOrCreate(FAKE_URL);

            assertThat(result).isSameAs(existingEntry);
        }

    }

    @Nested
    @DisplayName("Given I create a new URLEntry...")
    class CreateTest {

        @Test
        @DisplayName("with valid URL, then a new URLEntry is created, persisted and returned.")
        void withValidURLAndAlias_createsPersistsAndReturnsNewURLEntry() {
            URLEntry persistedEntry = URLEntryFixture.getDefault();
            doReturn(persistedEntry).when(urlEntryRepository).save(any(URLEntry.class));

            URLEntry result = urlEntryService.create(FAKE_URL);

            assertThat(result).isSameAs(persistedEntry);

            ArgumentCaptor<URLEntry> newEntryCaptor = ArgumentCaptor.forClass(URLEntry.class);
            verify(urlEntryRepository).save(newEntryCaptor.capture());
            assertThat(newEntryCaptor.getValue())
                .hasNoNullFieldsOrPropertiesExcept("id")
                .hasFieldOrPropertyWithValue("urlAddress", FAKE_URL);
        }

    }

    @Nested
    @DisplayName("Given I receive an URL address to shorten...")
    class ShortUrlTest {

        @Test
        @DisplayName("with no other pre-condition, then delegates to findAndCreate() method and returns the URL alias of its result.")
        void withNoOtherPreCondition_delegatesToFindOrCreateMethodToGenerateShortenedVersionAndReturnsTheAliasOfItsResult() {
            URLEntry existingEntry = URLEntryFixture.getDefault();
            String expectedAlias = Base62.encode(existingEntry.getId());
            doReturn(existingEntry).when(urlEntryService).findOrCreate(FAKE_URL);

            String result = urlEntryService.shortUrl(FAKE_URL);

            assertThat(result).isEqualTo(expectedAlias);
        }

    }

    @Nested
    @DisplayName("Given I look up for an URL based on its URL alias...")
    class FindUrlAddressByUrlAliasTest {

        @Test
        @DisplayName("for an existing URL entry, then returns its URL address.")
        void withExistingURLEntry_returnsUrlEntryUrlAddress() {
            URLEntry existingEntry = URLEntryFixture.getDefault();
            existingEntry.setId(Base62.decode(FAKE_ALIAS));
            doReturn(Optional.of(existingEntry)).when(urlEntryRepository).findById(existingEntry.getId());

            String result = urlEntryService.findUrlAddressByUrlAlias(FAKE_ALIAS);

            assertThat(result).isEqualTo(existingEntry.getUrlAddress());
        }

        @Test
        @DisplayName("for a non-existing URL entry, then throws a URLNotFoundException.")
        void withNonExistingURLEntry_throwsURLNotFoundException() {
            Long decodedId = Base62.decode(FAKE_ALIAS);
            doReturn(Optional.empty()).when(urlEntryRepository).findById(decodedId);
            assertThatThrownBy(() -> urlEntryService.findUrlAddressByUrlAlias(FAKE_ALIAS))
                .isInstanceOf(URLNotFoundException.class)
                .hasMessage(String.format("No URL found for the short version '%s'.", FAKE_ALIAS));
        }

    }

}
