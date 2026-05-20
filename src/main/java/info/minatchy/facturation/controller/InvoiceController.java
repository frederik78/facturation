package info.minatchy.facturation.controller;

import info.minatchy.facturation.model.*;
import info.minatchy.facturation.service.ClientService;
import info.minatchy.facturation.service.InvoiceService;
import info.minatchy.facturation.service.IssuerService;
import info.minatchy.facturation.service.PdfService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    public InvoiceController(InvoiceService invoiceService, ClientService clientService, IssuerService issuerService, PdfService pdfService) {
        this.invoiceService = invoiceService;
        this.clientService = clientService;
        this.issuerService = issuerService;
        this.pdfService = pdfService;
    }

    private final InvoiceService invoiceService;
    private final ClientService clientService;
    private final IssuerService issuerService;
    private final PdfService pdfService;

    // ── Liste ─────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(Model model) {
        model.addAttribute("invoices", invoiceService.findAll());
        return "invoices/list";
    }

    // ── Nouvelle facture ──────────────────────────────────────────────────────

    @GetMapping("/new")
    public String newForm(Model model) {
        if (issuerService.getIssuer().isEmpty()) {
            model.addAttribute("errorMessage",
                    "Vous devez d'abord configurer votre profil émetteur.");
            return "redirect:/settings";
        }
        model.addAttribute("clients", clientService.findAll());
        return "invoices/select-client";
    }

    @PostMapping("/new")
    public String createForClient(@RequestParam Long clientId, RedirectAttributes redirectAttrs) {
        Client client = clientService.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client introuvable : " + clientId));
        Invoice invoice = invoiceService.createNew(client);
        return "redirect:/invoices/" + invoice.getId() + "/edit";
    }

    // ── Édition ───────────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Invoice invoice = invoiceService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + id));
        model.addAttribute("invoice", invoice);
        model.addAttribute("statuses", InvoiceStatus.values());
        return "invoices/form";
    }

    @PostMapping("/{id}/save")
    public String save(@PathVariable Long id,
                       @RequestParam String invoiceNumber,
                       @RequestParam String invoiceDate,
                       @RequestParam String status,
                       @RequestParam(required = false) String notes,
                       // Lignes de prestation (tableaux parallèles)
                       @RequestParam(required = false) List<Long> itemIds,
                       @RequestParam(required = false) List<String> descriptions,
                       @RequestParam(required = false) List<String> details,
                       @RequestParam(required = false) List<String> quantities,
                       @RequestParam(required = false) List<String> units,
                       @RequestParam(required = false) List<String> unitPrices,
                       @RequestParam(required = false) List<String> periodStarts,
                       @RequestParam(required = false) List<String> periodEnds,
                       RedirectAttributes redirectAttrs) {

        Invoice invoice = invoiceService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + id));

        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setInvoiceDate(LocalDate.parse(invoiceDate));
        invoice.setStatus(InvoiceStatus.valueOf(status));
        invoice.setNotes(notes);

        // Reconstruit les lignes
        invoice.getItems().clear();
        if (descriptions != null) {
            for (int i = 0; i < descriptions.size(); i++) {
                String desc = descriptions.get(i);
                if (desc == null || desc.isBlank()) continue;

                InvoiceItem item = new InvoiceItem();
                item.setInvoice(invoice);
                item.setDescription(desc);
                item.setDetail(getOrNull(details, i));
                item.setUnit(getOrDefault(units, i, "h"));
                item.setQuantity(parseBigDecimal(getOrDefault(quantities, i, "1")));
                item.setUnitPrice(parseBigDecimal(getOrDefault(unitPrices, i, "0")));

                String ps = getOrNull(periodStarts, i);
                String pe = getOrNull(periodEnds, i);
                if (ps != null && !ps.isBlank()) item.setPeriodStart(LocalDate.parse(ps));
                if (pe != null && !pe.isBlank()) item.setPeriodEnd(LocalDate.parse(pe));

                invoice.getItems().add(item);
            }
        }

        invoiceService.save(invoice);
        redirectAttrs.addFlashAttribute("successMessage", "Facture enregistrée.");
        return "redirect:/invoices/" + id + "/edit";
    }

    // ── Téléchargement PDF ────────────────────────────────────────────────────

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws IOException {
        Invoice invoice = invoiceService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + id));

        byte[] pdf = pdfService.generateInvoicePdf(invoice);
        String filename = "Facture_" + invoice.getInvoiceNumber() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ── Suppression ───────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        invoiceService.delete(id);
        redirectAttrs.addFlashAttribute("successMessage", "Facture supprimée.");
        return "redirect:/invoices";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getOrNull(List<String> list, int index) {
        if (list == null || index >= list.size()) return null;
        String v = list.get(index);
        return (v == null || v.isBlank()) ? null : v;
    }

    private String getOrDefault(List<String> list, int index, String def) {
        if (list == null || index >= list.size()) return def;
        String v = list.get(index);
        return (v == null || v.isBlank()) ? def : v;
    }

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(s.replace(",", "."));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
