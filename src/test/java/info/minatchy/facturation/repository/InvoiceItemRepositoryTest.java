package info.minatchy.facturation.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.InvoiceItem;
import info.minatchy.facturation.model.Issuer;

@DataJpaTest
public class InvoiceItemRepositoryTest {

    @Autowired
    private InvoiceItemRepository itemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private IssuerRepository issuerRepository;

    @Test
    void saveItemAndAmountCalculation() {
        Client client = new Client(); client.setName("Client A"); clientRepository.save(client);
        Issuer issuer = new Issuer(); issuer.setCompanyName("Comp"); issuer.setContactName("C"); issuerRepository.save(issuer);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("2027001");
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setClient(client);
        invoice.setIssuer(issuer);
        invoiceRepository.save(invoice);

        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setDescription("Service");
        item.setQuantity(new BigDecimal("2.5"));
        item.setUnitPrice(new BigDecimal("100"));
        itemRepository.save(item);

        assertThat(item.getAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
    }
}
