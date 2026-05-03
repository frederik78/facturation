package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceStatusTest {

    @Test
    void labels() {
        assertEquals("Payée", InvoiceStatus.PAID.getLabel());
        assertEquals("Brouillon", InvoiceStatus.DRAFT.getLabel());
    }
}
