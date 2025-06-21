package com.addressbook.controller;

import com.addressbook.model.Contact;
import com.addressbook.model.ContactRequest;
import com.addressbook.model.ContactUpdateRequest;
import com.addressbook.model.SearchRequest;
import com.addressbook.service.ContactService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ContactControllerTest {

    @Mock
    private ContactService contactService;

    @InjectMocks
    private ContactController contactController;

    @Test
    @DisplayName("Should create contacts and return HTTP status CREATED")
    void shouldCreateContacts() {
        // Arrange
        ContactRequest request1 = new ContactRequest("John Doe", "1234567890", "john@example.com");
        ContactRequest request2 = new ContactRequest("Jane Smith", "0987654321", "jane@example.com");
        List<ContactRequest> contactRequests = Arrays.asList(request1, request2);

        Contact createdContact1 = new Contact("1", "John Doe", "1234567890", "john@example.com");
        Contact createdContact2 = new Contact("2", "Jane Smith", "0987654321", "jane@example.com");
        List<Contact> expectedContacts = Arrays.asList(createdContact1, createdContact2);

        when(contactService.createContacts(anyList())).thenReturn(expectedContacts);

        ResponseEntity<List<Contact>> response = contactController.createContacts(contactRequests);

        assertNotNull(response, "Response should not be null.");
        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "HTTP status should be CREATED.");
        assertNotNull(response.getBody(), "Response body should not be null.");
        assertEquals(2, response.getBody().size(), "Response body should contain 2 contacts.");
        assertEquals(expectedContacts, response.getBody(), "Returned contacts should match expected contacts.");
        verify(contactService).createContacts(anyList());
    }

    @Test
    @DisplayName("Should update contacts and return HTTP status OK")
    void shouldUpdateContacts() {
        ContactUpdateRequest updateRequest1 = new ContactUpdateRequest("1", "John Doe Updated", null, null);
        List<ContactUpdateRequest> updateRequests = Collections.singletonList(updateRequest1);

        Contact updatedContact1 = new Contact("1", "John Doe Updated", "1234567890", "john@example.com");
        List<Contact> expectedUpdatedContacts = Collections.singletonList(updatedContact1);

        when(contactService.updateContacts(anyList())).thenReturn(expectedUpdatedContacts);

        ResponseEntity<List<Contact>> response = contactController.updateContacts(updateRequests);

        assertNotNull(response, "Response should not be null.");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be OK.");
        assertNotNull(response.getBody(), "Response body should not be null.");
        assertEquals(1, response.getBody().size(), "Response body should contain 1 updated contact.");
        assertEquals(expectedUpdatedContacts, response.getBody(), "Returned updated contacts should match expected.");

        verify(contactService).updateContacts(anyList());
    }

    @Test
    @DisplayName("Should delete contacts and return HTTP status OK with deleted count")
    void shouldDeleteContacts() {
        List<String> contactIdsToDelete = Arrays.asList("1", "2");
        int deletedCount = 2;

        when(contactService.deleteContacts(anyList())).thenReturn(deletedCount);

        ResponseEntity<Map<String, Integer>> response = contactController.deleteContacts(contactIdsToDelete);

        assertNotNull(response, "Response should not be null.");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be OK.");
        assertNotNull(response.getBody(), "Response body should not be null.");
        assertEquals(deletedCount, response.getBody().get("deleted"), "Deleted count should match expected.");

        verify(contactService).deleteContacts(anyList());
    }

    @Test
    @DisplayName("Should search contacts and return HTTP status OK")
    void shouldSearchContacts() {
        // Arrange
        String searchQuery = "John";
        SearchRequest searchRequest = new SearchRequest(searchQuery);

        Contact foundContact1 = new Contact("1", "John Doe", "1234567890", "john@example.com");
        Contact foundContact2 = new Contact("3", "Johnny Bravo", "1122334455", "johnny@cartoon.net");
        List<Contact> expectedFoundContacts = Arrays.asList(foundContact1, foundContact2);

        when(contactService.searchContacts(anyString())).thenReturn(expectedFoundContacts);

        ResponseEntity<List<Contact>> response = contactController.searchContacts(searchRequest);

        assertNotNull(response, "Response should not be null.");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP status should be OK.");
        assertNotNull(response.getBody(), "Response body should not be null.");
        assertEquals(2, response.getBody().size(), "Response body should contain 2 contacts.");
        assertEquals(expectedFoundContacts, response.getBody(), "Returned contacts should match expected.");

        verify(contactService).searchContacts(anyString());
    }
}
