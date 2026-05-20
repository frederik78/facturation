package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceItemTest {

    @Test
    void amountAndPeriodLabel() {
        InvoiceItem it = new InvoiceItem();
        it.setQuantity(new BigDecimal("2.5"));
        it.setUnitPrice(new BigDecimal("10.00"));
        assertEquals(new BigDecimal("25.00"), it.getAmount());

        it.setPeriodStart(LocalDate.of(2026,4,1));
        it.setPeriodEnd(LocalDate.of(2026,4,3));
        assertEquals("Du 01/04/2026 au 03/04/2026", it.getPeriodLabel());

        InvoiceItem it2 = new InvoiceItem();
        it2.setDetail("Custom detail");
        assertEquals("Custom detail", it2.getPeriodLabel());

        InvoiceItem it3 = new InvoiceItem();
        assertEquals("", it3.getPeriodLabel());
    }
}
