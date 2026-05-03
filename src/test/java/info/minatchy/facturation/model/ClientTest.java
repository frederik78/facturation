package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void fullAddressCombinations() {
        Client c = new Client();
        c.setAddress("2 St. Clair Avenue West");
        c.setProvince("Ontario");
        c.setPostalCode("M4V 1L5");
        c.setCountry("Canada");

        String full = c.getFullAddress();
        assertTrue(full.contains("2 St. Clair"));
        assertTrue(full.contains("Ontario"));
        assertTrue(full.contains("M4V 1L5"));
        assertTrue(full.contains("Canada"));

        Client c2 = new Client();
        c2.setCountry("France");
        assertEquals("France", c2.getFullAddress());
    }
}
