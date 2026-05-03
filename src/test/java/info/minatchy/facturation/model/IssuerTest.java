package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IssuerTest {

    @Test
    void gettersAndSetters() {
        Issuer s = new Issuer();
        s.setCompanyName("ACME");
        s.setContactName("John Doe");
        s.setEmail("john@acme.example");
        assertEquals("ACME", s.getCompanyName());
        assertEquals("John Doe", s.getContactName());
        assertEquals("john@acme.example", s.getEmail());
    }
}
