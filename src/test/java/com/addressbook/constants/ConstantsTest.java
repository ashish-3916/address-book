package com.addressbook.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    @DisplayName("Should verify RequestMapping constants")
    void shouldVerifyRequestMappingConstants() {
        // Assert that the constants are not null and have the expected values.
        assertNotNull(Constants.RequestMapping.CREATE_CONTACT, "CREATE_CONTACT constant should not be null.");
        assertEquals("/create", Constants.RequestMapping.CREATE_CONTACT, "CREATE_CONTACT should be '/create'.");

        assertNotNull(Constants.RequestMapping.UPDATE_CONTACT, "UPDATE_CONTACT constant should not be null.");
        assertEquals("/update", Constants.RequestMapping.UPDATE_CONTACT, "UPDATE_CONTACT should be '/update'.");

        assertNotNull(Constants.RequestMapping.DELETE_CONTACT, "DELETE_CONTACT constant should not be null.");
        assertEquals("/delete", Constants.RequestMapping.DELETE_CONTACT, "DELETE_CONTACT should be '/delete'.");

        assertNotNull(Constants.RequestMapping.SEARCH_CONTACT, "SEARCH_CONTACT constant should not be null.");
        assertEquals("/search", Constants.RequestMapping.SEARCH_CONTACT, "SEARCH_CONTACT should be '/search'.");
    }
}