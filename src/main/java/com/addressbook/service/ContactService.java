package com.addressbook.service;

import com.addressbook.exception.ContactNotFoundException;
import com.addressbook.model.Contact;
import com.addressbook.model.ContactRequest;
import com.addressbook.model.ContactUpdateRequest;
import com.addressbook.repository.InMemoryContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ContactService {

    @Autowired
    private InMemoryContactRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    public List<Contact> createContacts(List<ContactRequest> contactRequests) {
        List<Contact> createdContacts = new ArrayList<>();
        for (ContactRequest request : contactRequests) {
            String id = UUID.randomUUID().toString();
            Contact contact = new Contact(id, request.getName(), request.getPhone(), request.getEmail());
            repository.save(contact);
            createdContacts.add(contact);
        }
        logger.info("Created {} contacts", createdContacts.size());
        return createdContacts;
    }

    public List<Contact> updateContacts(List<ContactUpdateRequest> contactUpdateRequests) {
        List<Contact> updatedContacts = new ArrayList<>();
        for (ContactUpdateRequest request : contactUpdateRequests) {
            Contact existingContact = repository.findById(request.getId())
                    .orElseThrow(() -> new ContactNotFoundException("Contact with ID " + request.getId() + " not found."));

            if (request.getName() != null) {
                existingContact.setName(request.getName());
            }
            if (request.getPhone() != null) {
                existingContact.setPhone(request.getPhone());
            }
            if (request.getEmail() != null) {
                existingContact.setEmail(request.getEmail());
            }
            repository.save(existingContact);
            updatedContacts.add(existingContact);
        }
        logger.info("Updated {} contacts", updatedContacts.size());
        return updatedContacts;
    }

    public int deleteContacts(List<String> contactIds) {
        int deletedCount = 0;
        for (String id : contactIds) {
            if (repository.deleteById(id)) {
                deletedCount++;
            }
        }
        logger.info("Deleted {} contacts", deletedCount);
        return deletedCount;
    }

    public List<Contact> searchContacts(String query) {
        return repository.search(query);
    }
}
