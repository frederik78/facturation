package info.minatchy.facturation.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceTotalsEmptyTest {

    @Test
    void emptyItemsProduceZeroTotals() {
        Invoice inv = new Invoice();
        assertEquals(new BigDecimal("0.00"), inv.getSubtotal());
        assertEquals(new BigDecimal("0.00"), inv.getTpsAmount());
        assertEquals(new BigDecimal("0.00"), inv.getTvqAmount());
        assertEquals(new BigDecimal("0.00"), inv.getTotal());
    }
}