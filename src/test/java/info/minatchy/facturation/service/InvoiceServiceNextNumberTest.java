package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceServiceNextNumberTest {



    @Test
    void nextNumberIncrementsExistingMax() {
        Invoice existing = new Invoice();
        existing.setInvoiceNumber(String.valueOf(LocalDate.now().getYear()) + "005");
        InvoiceRepository repo = info.minatchy.facturation.TestRepoFactory.invoiceRepoWithList(List.of(existing));

        IssuerService issuerSvc = new IssuerService(null) {
            @Override public java.util.Optional<Issuer> getIssuer() { return Optional.of(new Issuer()); }
        };

        InvoiceService svc = new InvoiceService(repo, issuerSvc);
        Client c = new Client(); c.setName("X");
        Invoice created = svc.createNew(c);
        // Expect increment of the long value
        long prev = Long.parseLong(existing.getInvoiceNumber());
        assertEquals(String.valueOf(prev + 1), created.getInvoiceNumber());
        assertEquals(1L, created.getId());
    }
}
