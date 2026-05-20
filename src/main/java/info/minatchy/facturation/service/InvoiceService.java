package info.minatchy.facturation.service;

import info.minatchy.facturation.model.*;
import info.minatchy.facturation.repository.InvoiceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    public InvoiceService(InvoiceRepository invoiceRepository, IssuerService issuerService) {
        this.invoiceRepository = invoiceRepository;
        this.issuerService = issuerService;
    }

    private final InvoiceRepository invoiceRepository;
    private final IssuerService issuerService;

    public List<Invoice> findAll() {
        return invoiceRepository.findAllByOrderByInvoiceDateDesc();
    }

    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    /**
     * Crée une nouvelle facture vide avec un numéro auto-généré.
     */
    @Transactional
    public Invoice createNew(Client client) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateNextInvoiceNumber());
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setClient(client);
        invoice.setStatus(InvoiceStatus.DRAFT);

        // Rattache l'émetteur (profil unique)
        Issuer issuer = issuerService.getIssuer()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun profil émetteur configuré. Veuillez d'abord configurer vos informations."));
        invoice.setIssuer(issuer);

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void delete(Long id) {
        invoiceRepository.deleteById(id);
    }

    @Transactional
    public Invoice updateStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + id));
        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }

    /**
     * Génère le prochain numéro de facture au format AAAANNN (ex: 2026001).
     */
    private String generateNextInvoiceNumber() {
        int year = LocalDate.now().getYear();
        String prefix = String.valueOf(year);

        // Cherche le max existant pour l'année courante
        long maxSeq = invoiceRepository.findAllByOrderByInvoiceDateDesc().stream()
                .map(Invoice::getInvoiceNumber)
                .filter(n -> n != null && n.startsWith(prefix))
                .mapToLong(n -> {
                    try { return Long.parseLong(n); } catch (NumberFormatException e) { return 0L; }
                })
                .max()
                .orElse(Long.parseLong(prefix + "000"));

        return String.valueOf(maxSeq + 1);
    }
}
