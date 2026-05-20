package info.minatchy.facturation.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.service.ClientService;
import info.minatchy.facturation.service.InvoiceService;
import info.minatchy.facturation.service.IssuerService;
import info.minatchy.facturation.service.PdfService;

@WebMvcTest(controllers = InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoiceService invoiceService;
    @MockitoBean
    private ClientService clientService;
    @MockitoBean
    private IssuerService issuerService;
    @MockitoBean
    private PdfService pdfService;

    @Test
    @WithMockUser
    void downloadPdfEndpoint() throws Exception {
        Issuer issuer = new Issuer(); issuer.setCompanyName("Co"); issuer.setContactName("P");
        Client client = new Client(); client.setName("Cli");
        Invoice invoice = new Invoice(); invoice.setId(1L); invoice.setInvoiceNumber("123"); invoice.setInvoiceDate(LocalDate.now()); invoice.setIssuer(issuer); invoice.setClient(client);

        when(invoiceService.findById(1L)).thenReturn(Optional.of(invoice));
        when(pdfService.generateInvoicePdf(invoice)).thenReturn(new byte[]{1,2,3});

        mockMvc.perform(get("/invoices/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("Facture_")))
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    @WithMockUser
    void newFormRedirectsIfNoIssuer() throws Exception {
        when(issuerService.getIssuer()).thenReturn(Optional.empty());
        mockMvc.perform(get("/invoices/new"))
                .andExpect(status().is3xxRedirection());
    }
}
