package com.addressbook.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContactNotFoundExceptionTest {

    @Test
    @DisplayName("Should set the correct exception message")
    void shouldSetCorrectMessage() {
        String expectedMessage = "Contact with ID '123' not found.";

        ContactNotFoundException exception = new ContactNotFoundException(expectedMessage);

        assertNotNull(exception.getMessage(), "Exception message should not be null.");
        assertEquals(expectedMessage, exception.getMessage(), "Exception message should match the provided message.");
    }

    @Test
    @DisplayName("Should have @ResponseStatus annotation with HttpStatus.NOT_FOUND")
    void shouldHaveResponseStatusNotFound() {
        Class<ContactNotFoundException> exceptionClass = ContactNotFoundException.class;
        ResponseStatus responseStatusAnnotation = exceptionClass.getAnnotation(ResponseStatus.class);
        assertNotNull(responseStatusAnnotation, "@ResponseStatus annotation should be present.");
        assertEquals(HttpStatus.NOT_FOUND, responseStatusAnnotation.value(),
                "@ResponseStatus value should be HttpStatus.NOT_FOUND.");
    }
}
