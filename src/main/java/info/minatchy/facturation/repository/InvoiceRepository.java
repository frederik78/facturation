

package info.minatchy.facturation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findAllByOrderByInvoiceDateDesc();

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByClientIdOrderByInvoiceDateDesc(Long clientId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    // findMaxInvoiceNumberAsLong removed: computing max invoice number is done in Java for portability.
}
