package info.minatchy.facturation.controller;

import info.minatchy.facturation.service.InvoiceService;
import info.minatchy.facturation.service.IssuerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    public HomeController(InvoiceService invoiceService, IssuerService issuerService) {
        this.invoiceService = invoiceService;
        this.issuerService = issuerService;
    }

    private final InvoiceService invoiceService;
    private final IssuerService issuerService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("invoices", invoiceService.findAll());
        model.addAttribute("issuerConfigured", issuerService.getIssuer().isPresent());
        return "index";
    }
}
