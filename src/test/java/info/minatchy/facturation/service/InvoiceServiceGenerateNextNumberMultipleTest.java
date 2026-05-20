package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceServiceGenerateNextNumberMultipleTest {



    @Test
    void choosesMaxNumericAndIncrements() {
        int year = java.time.LocalDate.now().getYear();
        Invoice good = new Invoice(); good.setInvoiceNumber(String.valueOf(year) + "009");
        Invoice bad = new Invoice(); bad.setInvoiceNumber(String.valueOf(year) + "ABC");

        InvoiceRepository repo = info.minatchy.facturation.TestRepoFactory.invoiceRepoWithList(List.of(good, bad));
        IssuerService issuerSvc = new IssuerService(null) { @Override public Optional<Issuer> getIssuer() { return Optional.of(new Issuer()); } };

        InvoiceService svc = new InvoiceService(repo, issuerSvc);
        Client c = new Client(); c.setName("C");
        Invoice created = svc.createNew(c);

        long prev = Long.parseLong(good.getInvoiceNumber());
        assertEquals(String.valueOf(prev + 1), created.getInvoiceNumber());
        assertEquals(1L, created.getId());
    }
}
