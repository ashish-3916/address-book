package com.addressbook.repository;

import com.addressbook.model.Contact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryContactRepositoryTest {

    private InMemoryContactRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryContactRepository();
    }

    @Test
    @DisplayName("Should save a new contact and retrieve it by ID")
    void shouldSaveNewContact() {
        Contact contact = new Contact("1", "John Doe", "1234567890", "john.doe@example.com");
        repository.save(contact);
        Optional<Contact> foundContact = repository.findById("1");
        assertTrue(foundContact.isPresent(), "Contact should be present after saving.");
        assertEquals(contact, foundContact.get(), "Retrieved contact should match the saved contact.");
    }

    @Test
    @DisplayName("Should update an existing contact")
    void shouldUpdateExistingContact() {
        Contact originalContact = new Contact("1", "John Doe", "1234567890", "john.doe@example.com");
        repository.save(originalContact);

        Contact updatedContact = new Contact("1", "Jane Doe", "0987654321", "jane.doe@example.com");
        repository.save(updatedContact);
        Optional<Contact> foundContact = repository.findById("1");
        assertTrue(foundContact.isPresent(), "Contact should be present after update.");
        assertEquals("Jane Doe", foundContact.get().getName(), "Contact name should be updated.");
        assertEquals("0987654321", foundContact.get().getPhone(), "Contact phone should be updated.");
        assertEquals("jane.doe@example.com", foundContact.get().getEmail(), "Contact email should be updated.");
    }

    @Test
    @DisplayName("Should find a contact by ID when it exists")
    void shouldFindContactByIdWhenExists() {
        Contact contact1 = new Contact("1", "Alice Smith", "1112223333", "alice@example.com");
        Contact contact2 = new Contact("2", "Bob Johnson", "4445556666", "bob@example.com");
        repository.save(contact1);
        repository.save(contact2);

        Optional<Contact> foundContact = repository.findById("1");

        assertTrue(foundContact.isPresent(), "Contact with ID '1' should be found.");
        assertEquals(contact1, foundContact.get(), "Found contact should be Alice Smith.");
    }

    @Test
    @DisplayName("Should return empty optional when contact by ID does not exist")
    void shouldReturnEmptyOptionalWhenContactByIdDoesNotExist() {
        Optional<Contact> foundContact = repository.findById("nonExistentId");
        assertFalse(foundContact.isPresent(), "No contact should be found for a non-existent ID.");
    }

    @Test
    @DisplayName("Should delete a contact by ID when it exists")
    void shouldDeleteContactByIdWhenExists() {
        Contact contact = new Contact("1", "Charlie Brown", "7778889999", "charlie@example.com");
        repository.save(contact);
        boolean isDeleted = repository.deleteById("1");

        assertTrue(isDeleted, "Delete operation should return true.");
        assertFalse(repository.findById("1").isPresent(), "Contact should no longer be present after deletion.");
    }

    @Test
    @DisplayName("Should not delete a contact by ID when it does not exist")
    void shouldNotDeleteContactByIdWhenDoesNotExist() {
        boolean isDeleted = repository.deleteById("nonExistentId");
        assertFalse(isDeleted, "Delete operation should return false for a non-existent ID.");
    }

    @Test
    @DisplayName("Should search contacts by name, phone, or email (case-insensitive for name/email)")
    void shouldSearchContacts() {
        Contact contact1 = new Contact("1", "Alice Smith", "111-222-3333", "alice.smith@example.com");
        Contact contact2 = new Contact("2", "Bob Johnson", "444-555-6666", "bob.j@mail.com");
        Contact contact3 = new Contact("3", "Charlie Day", "777-888-9999", "charlie.d@web.org");
        Contact contact4 = new Contact("4", "Alicea Keys", "999-888-7777", "keys@music.net");

        repository.save(contact1);
        repository.save(contact2);
        repository.save(contact3);
        repository.save(contact4);

        List<Contact> results1 = repository.search("alice smith");
        assertEquals(1, results1.size());
        assertTrue(results1.contains(contact1));

        List<Contact> results2 = repository.search("ALICE");
        assertEquals(2, results2.size()); // Alice Smith, Alicea Keys
        assertTrue(results2.contains(contact1));
        assertTrue(results2.contains(contact4));

        List<Contact> results3 = repository.search("444-555");
        assertEquals(1, results3.size());
        assertTrue(results3.contains(contact2));

        List<Contact> results4 = repository.search("example.com");
        assertEquals(1, results4.size());
        assertTrue(results4.contains(contact1));

        List<Contact> results5 = repository.search("mail.com");
        assertEquals(1, results5.size());
        assertTrue(results5.contains(contact2));

        List<Contact> results6 = repository.search("nonexistent");
        assertTrue(results6.isEmpty());

        List<Contact> results7 = repository.search("");
        assertEquals(4, results7.size());
    }

    @Test
    @DisplayName("Should return empty list when searching in an empty repository")
    void shouldReturnEmptyListWhenSearchingInEmptyRepository() {
        List<Contact> results = repository.search("any query");
        assertTrue(results.isEmpty(), "Searching an empty repository should return an empty list.");
    }
}
