package com.addressbook.service;

import com.addressbook.exception.ContactNotFoundException;
import com.addressbook.model.Contact;
import com.addressbook.model.ContactRequest;
import com.addressbook.model.ContactUpdateRequest;
import com.addressbook.repository.InMemoryContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private InMemoryContactRepository repository;

    @InjectMocks
    private ContactService contactService;

    @Test
    @DisplayName("Should create multiple contacts successfully")
    void shouldCreateMultipleContacts() {
        ContactRequest request1 = new ContactRequest("Alice", "111", "alice@example.com");
        ContactRequest request2 = new ContactRequest("Bob", "222", "bob@example.com");
        List<ContactRequest> contactRequests = Arrays.asList(request1, request2);

        List<Contact> createdContacts = contactService.createContacts(contactRequests);

        assertNotNull(createdContacts, "Created contacts list should not be null.");
        assertEquals(2, createdContacts.size(), "Should create 2 contacts.");

        createdContacts.forEach(contact -> assertNotNull(contact.getId(), "Contact ID should not be null."));

        verify(repository, times(2)).save(any(Contact.class));
    }

    @Test
    @DisplayName("Should update an existing contact with all fields")
    void shouldUpdateExistingContactWithAllFields() {
        String contactId = "existing-id-123";
        Contact existingContact = new Contact(contactId, "Old Name", "Old Phone", "old@example.com");
        ContactUpdateRequest updateRequest = new ContactUpdateRequest(contactId, "New Name", "New Phone", "new@example.com");
        List<ContactUpdateRequest> updateRequests = Collections.singletonList(updateRequest);

        when(repository.findById(contactId)).thenReturn(Optional.of(existingContact));
        List<Contact> updatedContacts = contactService.updateContacts(updateRequests);

        assertNotNull(updatedContacts, "Updated contacts list should not be null.");
        assertEquals(1, updatedContacts.size(), "Should update 1 contact.");
        Contact updatedContact = updatedContacts.get(0);
        assertEquals(contactId, updatedContact.getId(), "Updated contact ID should match.");
        assertEquals("New Name", updatedContact.getName(), "Contact name should be updated.");
        assertEquals("New Phone", updatedContact.getPhone(), "Contact phone should be updated.");
        assertEquals("new@example.com", updatedContact.getEmail(), "Contact email should be updated.");

        verify(repository, times(1)).findById(contactId);
        verify(repository, times(1)).save(updatedContact);
    }

    @Test
    @DisplayName("Should throw ContactNotFoundException if contact to update is not found")
    void shouldThrowContactNotFoundExceptionWhenUpdatingNonExistentContact() {
        // Arrange
        String nonExistentId = "non-existent-id";
        ContactUpdateRequest updateRequest = new ContactUpdateRequest(nonExistentId, "Name", "Phone", "email@example.com");
        List<ContactUpdateRequest> updateRequests = Collections.singletonList(updateRequest);

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        ContactNotFoundException thrown = assertThrows(ContactNotFoundException.class, () -> {
            contactService.updateContacts(updateRequests);
        }, "ContactNotFoundException should be thrown.");

        assertEquals("Contact with ID " + nonExistentId + " not found.", thrown.getMessage(),
                "Exception message should be correct.");

        verify(repository, never()).save(any(Contact.class));
    }

    @Test
    @DisplayName("Should delete multiple existing contacts successfully")
    void shouldDeleteMultipleExistingContacts() {
        List<String> contactIds = Arrays.asList("id1", "id2", "id3");

        when(repository.deleteById("id1")).thenReturn(true);
        when(repository.deleteById("id2")).thenReturn(true);
        when(repository.deleteById("id3")).thenReturn(true);

        int deletedCount = contactService.deleteContacts(contactIds);

        assertEquals(3, deletedCount, "Should delete 3 contacts.");

        verify(repository, times(1)).deleteById("id1");
        verify(repository, times(1)).deleteById("id2");
        verify(repository, times(1)).deleteById("id3");
    }


    @Test
    @DisplayName("Should delete only existing contacts and return correct count")
    void shouldDeleteMixOfExistingAndNonExistingContacts() {
        List<String> contactIds = Arrays.asList("id1", "id_non_existent", "id2");

        when(repository.deleteById("id1")).thenReturn(true);
        when(repository.deleteById("id_non_existent")).thenReturn(false);
        when(repository.deleteById("id2")).thenReturn(true);

        int deletedCount = contactService.deleteContacts(contactIds);

        assertEquals(2, deletedCount, "Should delete 2 contacts (id1 and id2).");

        verify(repository, times(1)).deleteById("id1");
        verify(repository, times(1)).deleteById("id_non_existent");
        verify(repository, times(1)).deleteById("id2");
    }


    @Test
    @DisplayName("Should return 0 when deleting an empty list of IDs")
    void shouldReturnZeroWhenDeletingEmptyListOfIds() {
        List<String> contactIds = Collections.emptyList();

        int deletedCount = contactService.deleteContacts(contactIds);

        assertEquals(0, deletedCount, "Deleted count should be 0 for an empty list.");

        verify(repository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Should search contacts via repository")
    void shouldSearchContacts() {
        String searchQuery = "test_query";
        Contact foundContact1 = new Contact("c1", "Test Name", "123", "test@email.com");
        Contact foundContact2 = new Contact("c2", "Another Test", "456", "another@email.com");
        List<Contact> expectedResults = Arrays.asList(foundContact1, foundContact2);

        when(repository.search(searchQuery)).thenReturn(expectedResults);

        List<Contact> actualResults = contactService.searchContacts(searchQuery);

        assertNotNull(actualResults, "Search results should not be null.");
        assertEquals(expectedResults.size(), actualResults.size(), "Number of results should match.");
        assertEquals(expectedResults, actualResults, "Returned contacts should match expected.");

        verify(repository, times(1)).search(searchQuery);
    }


    @Test
    @DisplayName("Should return empty list when no contacts match search query")
    void shouldReturnEmptyListWhenNoContactsMatchSearch() {
        String searchQuery = "non_existent_query";

        when(repository.search(searchQuery)).thenReturn(Collections.emptyList());

        List<Contact> actualResults = contactService.searchContacts(searchQuery);

        assertNotNull(actualResults, "Search results should not be null.");
        assertTrue(actualResults.isEmpty(), "Results list should be empty.");
        verify(repository, times(1)).search(searchQuery);
    }
}
