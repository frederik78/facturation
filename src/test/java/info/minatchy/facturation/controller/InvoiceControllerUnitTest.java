package info.minatchy.facturation.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.service.ClientService;
import info.minatchy.facturation.service.InvoiceService;
import info.minatchy.facturation.service.IssuerService;
import info.minatchy.facturation.service.PdfService;

public class InvoiceControllerUnitTest {

    @Test
    void newFormRedirectsWhenNoIssuer() {
        IssuerService issuerSvc = new IssuerService(null) { @Override public Optional<Issuer> getIssuer() { return Optional.empty(); } };
        InvoiceController ctrl = new InvoiceController(null, null, issuerSvc, null);
        Model model = new ConcurrentModel();
        String view = ctrl.newForm(model);
        assertEquals("redirect:/settings", view);
        assertTrue(model.containsAttribute("errorMessage"));
    }

    @Test
    void newFormShowsClientsWhenIssuerPresent() {
        IssuerService issuerSvc = new IssuerService(null) { @Override public Optional<Issuer> getIssuer() { return Optional.of(new Issuer()); } };
        ClientService clientSvc = new ClientService(null) { @Override public List<Client> findAll() { return List.of(new Client()); } };
        InvoiceController ctrl = new InvoiceController(null, clientSvc, issuerSvc, null);
        Model model = new ConcurrentModel();
        String view = ctrl.newForm(model);
        assertEquals("invoices/select-client", view);
        assertTrue(model.containsAttribute("clients"));
    }

    @Test
    void createForClientWorksAndThrowsWhenMissing() {
        Client client = new Client(); client.setId(2L);
        ClientService clientSvc = new ClientService(null) { @Override public Optional<Client> findById(Long id) { return Optional.of(client); } };
        Invoice inv = new Invoice(); inv.setId(9L);
        InvoiceService invSvc = new InvoiceService(null, null) { @Override public Invoice createNew(Client c) { inv.setClient(c); return inv; } };

        InvoiceController ctrl = new InvoiceController(invSvc, clientSvc, null, null);
        RedirectAttributesModelMap attrs = new RedirectAttributesModelMap();
        String res = ctrl.createForClient(2L, attrs);
        assertEquals("redirect:/invoices/9/edit", res);

        ClientService missing = new ClientService(null) { @Override public Optional<Client> findById(Long id) { return Optional.empty(); } };
        InvoiceController ctrl2 = new InvoiceController(null, missing, null, null);
        assertThrows(IllegalArgumentException.class, () -> ctrl2.createForClient(1L, new RedirectAttributesModelMap()));
    }

    @Test
    void editFormAndDownloadPdfAndDelete() throws IOException {
        Invoice inv = new Invoice(); inv.setId(3L); inv.setInvoiceNumber("2026001"); inv.setInvoiceDate(LocalDate.now());
        inv.setIssuer(new Issuer()); inv.setClient(new Client());
        InvoiceService invSvc = new InvoiceService(null, null) { @Override public Optional<Invoice> findById(Long id) { return Optional.of(inv); } };

        PdfService pdf = new PdfService() { @Override public byte[] generateInvoicePdf(Invoice invoice) { return new byte[]{1,2,3}; } };
        InvoiceController ctrl = new InvoiceController(invSvc, null, null, pdf);

        Model model = new ConcurrentModel();
        String view = ctrl.editForm(3L, model);
        assertEquals("invoices/form", view);
        assertTrue(model.containsAttribute("invoice"));
        assertTrue(model.containsAttribute("statuses"));

        ResponseEntity<byte[]> resp = ctrl.downloadPdf(3L);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, resp.getHeaders().getContentType());
        assertTrue(resp.getHeaders().getFirst("Content-Disposition").contains("Facture_"));
        assertArrayEquals(new byte[]{1,2,3}, resp.getBody());

        AtomicReference<Long> deleted = new AtomicReference<>();
        InvoiceService svcDel = new InvoiceService(null, null) { @Override public void delete(Long id) { deleted.set(id); } };
        InvoiceController ctrlDel = new InvoiceController(svcDel, null, null, null);
        RedirectAttributesModelMap attrs = new RedirectAttributesModelMap();
        String dv = ctrlDel.delete(5L, attrs);
        assertEquals("redirect:/invoices", dv);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    void saveBuildsItemsAndSaves() {
        Invoice base = new Invoice(); base.setId(11L); base.setInvoiceDate(LocalDate.of(2026,4,1));
        InvoiceService invSvc = new InvoiceService(null, null) {
            @Override public Optional<Invoice> findById(Long id) { return Optional.of(base); }
            @Override public Invoice save(Invoice invoice) { return invoice; }
        };

        InvoiceController ctrl = new InvoiceController(invSvc, null, null, null);
        RedirectAttributesModelMap attrs = new RedirectAttributesModelMap();

        List<String> desc = List.of("Service A", "", "Service B");
        List<String> quantities = List.of("2", "", "3.5");
        List<String> units = List.of("h", "h", "j");
        List<String> unitPrices = List.of("10", "0", "100,50");

        String res = ctrl.save(11L, "2026001", "2026-04-01", "DRAFT", "Notes",
                null, desc, null, quantities, units, unitPrices, null, null, attrs);

        assertEquals("redirect:/invoices/11/edit", res);
        assertTrue(attrs.getFlashAttributes().containsKey("successMessage"));
        // verify items constructed
        assertEquals(2, base.getItems().size());
        assertEquals("Service A", base.getItems().get(0).getDescription());
        assertEquals(new BigDecimal("2"), base.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("10"), base.getItems().get(0).getUnitPrice());
        assertEquals("Service B", base.getItems().get(1).getDescription());
        assertEquals(new BigDecimal("3.5"), base.getItems().get(1).getQuantity());
        assertEquals(new BigDecimal("100.50"), base.getItems().get(1).getUnitPrice());
    }
}
