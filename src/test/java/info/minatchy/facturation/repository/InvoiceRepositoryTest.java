package info.minatchy.facturation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.InvoiceStatus;
import info.minatchy.facturation.model.Issuer;

@DataJpaTest
public class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private IssuerRepository issuerRepository;

    @Test
    void saveAndFindMethods() {
        Client client = new Client(); client.setName("Client X"); clientRepository.save(client);
        Issuer issuer = new Issuer(); issuer.setCompanyName("C"); issuer.setContactName("C"); issuerRepository.save(issuer);

        Invoice inv = new Invoice();
        inv.setInvoiceNumber("2026001");
        inv.setInvoiceDate(LocalDate.now());
        inv.setClient(client);
        inv.setIssuer(issuer);
        inv.setStatus(InvoiceStatus.DRAFT);

        invoiceRepository.save(inv);

        List<Invoice> all = invoiceRepository.findAllByOrderByInvoiceDateDesc();
        assertThat(all).isNotEmpty();

        Optional<Invoice> byNum = invoiceRepository.findByInvoiceNumber("2026001");
        assertThat(byNum).isPresent();

        List<Invoice> byStatus = invoiceRepository.findByStatus(InvoiceStatus.DRAFT);
        assertThat(byStatus).isNotEmpty();
    }
}
