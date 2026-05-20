package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private IssuerService issuerService;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void createNewUsesIssuerAndSaves() {
        Client client = new Client(); client.setName("C");
        Issuer issuer = new Issuer(); issuer.setCompanyName("Co"); issuer.setContactName("P");

        when(issuerService.getIssuer()).thenReturn(Optional.of(issuer));
        when(invoiceRepository.findAllByOrderByInvoiceDateDesc()).thenReturn(Collections.emptyList());

        Invoice saved = new Invoice(); saved.setId(1L);
        when(invoiceRepository.save(any())).thenReturn(saved);

        Invoice created = invoiceService.createNew(client);
        assertThat(created.getId()).isEqualTo(1L);
        verify(invoiceRepository).save(any());
    }

    @Test
    void updateStatusThrowsIfMissing() {
        when(invoiceRepository.findById(5L)).thenReturn(Optional.empty());
        try {
            invoiceService.updateStatus(5L, null);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Facture introuvable");
        }
    }
}
