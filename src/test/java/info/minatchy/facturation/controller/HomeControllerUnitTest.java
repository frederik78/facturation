package info.minatchy.facturation.controller;

import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.service.InvoiceService;
import info.minatchy.facturation.service.IssuerService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class HomeControllerUnitTest {

    @Test
    void homeAddsInvoicesAndIssuerFlag() {
        Invoice inv = new Invoice(); inv.setInvoiceNumber("2026001"); inv.setInvoiceDate(LocalDate.now());
        InvoiceService invoiceStub = new InvoiceService(null, null) {
            @Override
            public java.util.List<Invoice> findAll() { return List.of(inv); }
        };

        Issuer issuer = new Issuer(); issuer.setCompanyName("Co");
        IssuerService issuerStub = new IssuerService(null) {
            @Override
            public Optional<Issuer> getIssuer() { return Optional.of(issuer); }
        };

        HomeController ctrl = new HomeController(invoiceStub, issuerStub);
        Model model = new ConcurrentModel();
        String view = ctrl.home(model);
        assertEquals("index", view);
        assertTrue(model.containsAttribute("invoices"));
        assertTrue(model.containsAttribute("issuerConfigured"));
        assertEquals(Boolean.TRUE, model.getAttribute("issuerConfigured"));
    }
}
