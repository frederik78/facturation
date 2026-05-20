package info.minatchy.facturation.service;

import info.minatchy.facturation.model.Client;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class InvoiceServiceNoIssuerTest {



    @Test
    void createNewThrowsWhenNoIssuer() {
        InvoiceService svc = new InvoiceService(info.minatchy.facturation.TestRepoFactory.invoiceRepoEmpty(), new IssuerService(null) { @Override public java.util.Optional<info.minatchy.facturation.model.Issuer> getIssuer() { return java.util.Optional.empty(); } });
        Client c = new Client(); c.setName("X");
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> svc.createNew(c));
        assertNotNull(exception);
    }
}
