package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceServiceFindersTest {



    @Test
    void findAllAndFindByIdDelegates() {
        Invoice inv = new Invoice(); inv.setId(5L);
        InvoiceRepository repo = info.minatchy.facturation.TestRepoFactory.invoiceRepoWithList(List.of(inv));
        InvoiceService svc = new InvoiceService(repo, null);
        assertFalse(svc.findAll().isEmpty());
        Optional<Invoice> maybe = svc.findById(5L);
        assertTrue(maybe.isPresent());
        assertEquals(5L, maybe.get().getId());
    }
}
