package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    @Test
    void calculations() {
        Invoice inv = new Invoice();
        inv.setInvoiceDate(LocalDate.now());

        InvoiceItem i1 = new InvoiceItem();
        i1.setQuantity(new BigDecimal("1"));
        i1.setUnitPrice(new BigDecimal("100.00"));

        InvoiceItem i2 = new InvoiceItem();
        i2.setQuantity(new BigDecimal("2"));
        i2.setUnitPrice(new BigDecimal("50.00"));

        i1.setInvoice(inv); i2.setInvoice(inv);
        inv.setItems(List.of(i1, i2));

        assertEquals(new BigDecimal("200.00"), inv.getSubtotal());
        // TPS = 5% -> 10.00
        assertEquals(new BigDecimal("10.00"), inv.getTpsAmount());
        // TVQ = 9.975% -> 19.95 (200 * 0.09975 = 19.95)
        assertEquals(new BigDecimal("19.95"), inv.getTvqAmount());
        assertEquals(new BigDecimal("229.95"), inv.getTotal());
    }
}
