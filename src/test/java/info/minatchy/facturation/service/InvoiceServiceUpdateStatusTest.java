package info.minatchy.facturation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.InvoiceStatus;
import info.minatchy.facturation.repository.InvoiceRepository;

public class InvoiceServiceUpdateStatusTest {



    @Test
    void updateStatusSuccess() {
        Invoice inv = new Invoice(); inv.setId(10L); inv.setStatus(InvoiceStatus.DRAFT);
        InvoiceRepository repo = info.minatchy.facturation.TestRepoFactory.invoiceRepoWithFindById(inv);
        InvoiceService svc = new InvoiceService(repo, null);

        Invoice updated = svc.updateStatus(10L, InvoiceStatus.PAID);
        assertEquals(InvoiceStatus.PAID, updated.getStatus());
    }
}
