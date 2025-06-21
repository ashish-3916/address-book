package com.addressbook.controller;

import com.addressbook.constants.Constants;
import com.addressbook.model.Contact;
import com.addressbook.model.ContactRequest;
import com.addressbook.model.ContactUpdateRequest;
import com.addressbook.model.SearchRequest;
import com.addressbook.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @PostMapping(Constants.RequestMapping.CREATE_CONTACT)
    public ResponseEntity<List<Contact>> createContacts(@RequestBody List<ContactRequest> contactRequests) {
        logger.info("Creating contacts from {}", contactRequests);
        List<Contact> createdContacts = contactService.createContacts(contactRequests);
        return new ResponseEntity<>(createdContacts, HttpStatus.CREATED);
    }

    @PutMapping(Constants.RequestMapping.UPDATE_CONTACT)
    public ResponseEntity<List<Contact>> updateContacts(@RequestBody List<ContactUpdateRequest> contactUpdateRequests) {
        logger.info("Updating contacts from {}", contactUpdateRequests);
        List<Contact> updatedContacts = contactService.updateContacts(contactUpdateRequests);
        return new ResponseEntity<>(updatedContacts, HttpStatus.OK);
    }

    @DeleteMapping(Constants.RequestMapping.DELETE_CONTACT)
    public ResponseEntity<Map<String, Integer>> deleteContacts(@RequestBody List<String> contactIds) {
        logger.info("Deleting contacts from {}", contactIds);
        int deletedCount = contactService.deleteContacts(contactIds);
        return new ResponseEntity<>(Collections.singletonMap("deleted", deletedCount), HttpStatus.OK);
    }

    @PostMapping(Constants.RequestMapping.SEARCH_CONTACT)
    public ResponseEntity<List<Contact>> searchContacts(@RequestBody SearchRequest searchRequest) {
        logger.info("Searching contacts from {}", searchRequest);
        List<Contact> foundContacts = contactService.searchContacts(searchRequest.getQuery());
        return new ResponseEntity<>(foundContacts, HttpStatus.OK);
    }
}
