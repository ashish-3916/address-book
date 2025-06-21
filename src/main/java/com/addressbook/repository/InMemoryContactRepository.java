package com.addressbook.repository;

import com.addressbook.controller.ContactController;
import com.addressbook.model.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
public class InMemoryContactRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryContactRepository.class);

    private final Map<String, Contact> contacts = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Optional<Contact> findById(String id) {
        lock.readLock().lock();
        try {
            logger.info("Find contact by id {}", id);
            return Optional.ofNullable(contacts.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }
    public void save(Contact contact) {
        lock.writeLock().lock();
        try {
            logger.info("Save contact {}", contact);
            contacts.put(contact.getId(), contact);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteById(String id) {
        lock.writeLock().lock();
        try {
            Contact removedContact = contacts.remove(id);
            logger.info("Removed contact {}", removedContact);
            return removedContact != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Contact> search(String query) {
        lock.readLock().lock();
        try {

            return contacts.values().stream()
                    .filter(contact -> contact.getName().toLowerCase().contains(query.toLowerCase()) ||
                            contact.getPhone().contains(query) ||
                            contact.getEmail().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
}
