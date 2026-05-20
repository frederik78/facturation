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
    private Issuer fullIssuer() {
        Issuer i = new Issuer();
        i.setCompanyName("ACME Inc.");
        i.setContactName("Jean Dupont");
        i.setEmail("jean@acme.com");
        i.setPhone("514-000-0000");
        i.setTpsNumber("123456789 RT0001");
        i.setTvqNumber("1234567890 TQ0001");
        return i;
    }

    private Issuer minimalIssuer() {
        Issuer i = new Issuer();
        i.setCompanyName("Co");
        i.setContactName("P");
        return i;
    }

    private Client fullClient() {
        Client c = new Client();
        c.setName("Client SA");
        c.setAddress("123 rue Principale");
        c.setProvince("QC");
        c.setPostalCode("H1A 1A1");
        c.setCountry("Canada");
        return c;
    }

    private Client minimalClient() {
        Client c = new Client();
        c.setName("Client");
        return c;
    }

    private Invoice buildInvoice(Issuer issuer, Client client) {
        Invoice inv = new Invoice();
        inv.setInvoiceNumber("2026-TEST");
        inv.setInvoiceDate(LocalDate.of(2026, 1, 15));
        inv.setIssuer(issuer);
        inv.setClient(client);
        return inv;
    }

    private InvoiceItem item(String desc, String detail) {
        InvoiceItem it = new InvoiceItem();
        it.setDescription(desc);
        it.setDetail(detail);          // "period" → detail
        it.setQuantity(new BigDecimal("8"));
        it.setUnitPrice(new BigDecimal("100"));
        it.setUnit("h");
        return it;
    }

    private InvoiceItem itemWithPeriod(String desc, LocalDate start, LocalDate end) {
        InvoiceItem it = new InvoiceItem();
        it.setDescription(desc);
        it.setPeriodStart(start);
        it.setPeriodEnd(end);
        it.setQuantity(new BigDecimal("8"));
        it.setUnitPrice(new BigDecimal("100"));
        it.setUnit("h");
        return it;
    }


    @Test
    void fullIssuerFullClientWithPeriod() throws Exception {
        Invoice inv = buildInvoice(fullIssuer(), fullClient());
        inv.getItems().add(item("Développement", "Janvier 2026"));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    @Test
    void minimalIssuerMinimalClientNoPeriod() throws Exception {
        Invoice inv = buildInvoice(minimalIssuer(), minimalClient());
        inv.getItems().add(item("Service", null));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    @Test
    void itemWithBlankPeriod() throws Exception {
        Invoice inv = buildInvoice(fullIssuer(), fullClient());
        inv.getItems().add(item("Service", "   "));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    /** detail non null → getPeriodLabel() retourne detail */
    @Test
    void fullIssuerFullClientWithDetail() throws Exception {
        Invoice inv = buildInvoice(fullIssuer(), fullClient());
        inv.getItems().add(item("Développement", "Janvier 2026"));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    /** detail null → getPeriodLabel() retourne "" → branch isBlank dans PdfService */
    @Test
    void minimalIssuerMinimalClientNullDetail() throws Exception {
        Invoice inv = buildInvoice(minimalIssuer(), minimalClient());
        inv.getItems().add(item("Service", null));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    /** detail blank → getPeriodLabel() retourne "" */
    @Test
    void itemWithBlankDetail() throws Exception {
        Invoice inv = buildInvoice(fullIssuer(), fullClient());
        inv.getItems().add(item("Service", "   "));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    /** periodStart + periodEnd → getPeriodLabel() retourne "Du xx/xx/xxxx au xx/xx/xxxx" */
    @Test
    void itemWithPeriodStartEnd() throws Exception {
        Invoice inv = buildInvoice(fullIssuer(), fullClient());
        inv.getItems().add(itemWithPeriod(
                "Développement",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        ));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    @Test
    void clientProvinceOnlyNoPostalCode() throws Exception {
        Client c = fullClient();
        c.setPostalCode(null);
        Invoice inv = buildInvoice(fullIssuer(), c);
        inv.getItems().add(item("Service", "Période"));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    @Test
    void clientPostalCodeOnlyNoProvince() throws Exception {
        Client c = fullClient();
        c.setProvince(null);
        Invoice inv = buildInvoice(fullIssuer(), c);
        inv.getItems().add(item("Service", "Période"));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    @Test
    void invoiceWithNoItems() throws Exception {
        Invoice inv = buildInvoice(fullIssuer(), fullClient());

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

    @Test
    void multipleItems() throws Exception {
        Invoice inv = buildInvoice(fullIssuer(), fullClient());
        inv.getItems().add(itemWithPeriod("Développement", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)));
        inv.getItems().add(item("Réunion", null));
        inv.getItems().add(item("Analyse", "Sprint 3"));

        byte[] pdf = pdfService.generateInvoicePdf(inv);
        assertThat(pdf).isNotNull().hasSizeGreaterThan(100);
    }

}
