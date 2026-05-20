package info.minatchy.facturation.model;

public enum InvoiceStatus {
    DRAFT("Brouillon"),
    SENT("Envoyée"),
    PAID("Payée"),
    CANCELLED("Annulée");

    private final String label;

    InvoiceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
