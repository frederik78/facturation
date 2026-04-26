package info.minatchy.facturation.repository;

import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findAllByOrderByInvoiceDateDesc();

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByClientIdOrderByInvoiceDateDesc(Long clientId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    // findMaxInvoiceNumberAsLong removed: computing max invoice number is done in Java for portability.
}
