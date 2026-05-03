package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.InvoiceItem;
import info.minatchy.facturation.model.Issuer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class PdfServiceTest {

    private final PdfService pdfService = new PdfService();

    @Test
    void generatePdfProducesBytes() throws Exception {
        Issuer issuer = new Issuer(); issuer.setCompanyName("Co"); issuer.setContactName("P");
        Client client = new Client(); client.setName("Client");

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("2026001");
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setIssuer(issuer);
        invoice.setClient(client);

        InvoiceItem item = new InvoiceItem();
        item.setDescription("Service");
        item.setQuantity(new BigDecimal("2"));
        item.setUnitPrice(new BigDecimal("10"));
        invoice.getItems().add(item);

        byte[] pdf = pdfService.generateInvoicePdf(invoice);
        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(100);
    }
}
