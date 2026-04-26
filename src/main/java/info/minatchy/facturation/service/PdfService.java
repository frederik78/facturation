package info.minatchy.facturation.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.model.InvoiceItem;
import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.model.Client;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service de génération PDF fidèle à la mise en page de la facture originale.
 *
 * Mise en page :
 *  - En-tête centré : FACTURE / #numéro / date
 *  - Deux colonnes : émetteur (gauche) | FACTURÉ À (droite)
 *  - Tableau des prestations
 *  - Lignes TPS / TVQ
 *  - Bande noire TOTAL
 */
@Service
public class PdfService {

    // Couleurs
    private static final DeviceRgb BLACK       = new DeviceRgb(0, 0, 0);
    private static final DeviceRgb LIGHT_GRAY  = new DeviceRgb(220, 220, 220);
    private static final DeviceRgb DARK_GRAY   = new DeviceRgb(80, 80, 80);

    // Marges de la page (points)
    private static final float MARGIN = 50f;

    // Formatter monétaire canadien-français
    private static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getInstance(Locale.CANADA_FRENCH);
    static {
        CURRENCY_FORMAT.setMinimumFractionDigits(2);
        CURRENCY_FORMAT.setMaximumFractionDigits(2);
    }

    // Formatter de date français
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.CANADA_FRENCH);

    /**
     * Génère le PDF d'une facture en mémoire et retourne les bytes.
     */
    public byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

        // Polices
        PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold    = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

        Issuer issuer = invoice.getIssuer();
        Client client = invoice.getClient();

        // ── 1. En-tête centré ─────────────────────────────────────────────────
        addHeader(document, invoice, fontBold, fontRegular);

        // Espace
        document.add(new Paragraph("\n"));

        // ── 2. Bloc émetteur / client ──────────────────────────────────────────
        addIssuerClientBlock(document, issuer, client, fontBold, fontRegular);

        // Espace
        document.add(new Paragraph("\n\n"));

        // ── 3. Tableau des prestations + taxes + total ─────────────────────────
        addItemsTable(document, invoice, fontBold, fontRegular);

        document.close();
        return baos.toByteArray();
    }

    // ── Méthodes privées ──────────────────────────────────────────────────────

    private void addHeader(Document doc, Invoice invoice, PdfFont bold, PdfFont regular) {
        // Titre FACTURE
        Paragraph title = new Paragraph("FACTURE")
                .setFont(bold)
                .setFontSize(26f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4f);
        doc.add(title);

        // Numéro
        Paragraph num = new Paragraph("#" + invoice.getInvoiceNumber())
                .setFont(bold)
                .setFontSize(11f)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2f);
        doc.add(num);

        // Date
        String dateStr = invoice.getInvoiceDate() != null
                ? invoice.getInvoiceDate().format(DATE_FORMATTER)
                : LocalDate.now().format(DATE_FORMATTER);
        Paragraph date = new Paragraph(dateStr)
                .setFont(regular)
                .setFontSize(11f)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(date);
    }

    private void addIssuerClientBlock(Document doc, Issuer issuer, Client client,
                                      PdfFont bold, PdfFont regular) {
        // Table 2 colonnes égales
        Table table = new Table(UnitValue.createPercentArray(new float[]{50f, 50f}))
                .setWidth(UnitValue.createPercentValue(100));

        // Colonne gauche : émetteur
        Cell leftCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(0);

        leftCell.add(new Paragraph(issuer.getCompanyName())
                .setFont(bold).setFontSize(11f).setMarginBottom(2f));
        leftCell.add(new Paragraph(issuer.getContactName())
                .setFont(regular).setFontSize(10f).setMarginBottom(8f));

        if (issuer.getEmail() != null && !issuer.getEmail().isBlank()) {
            leftCell.add(buildLabelValue("Courriel : ", issuer.getEmail(), bold, regular, 10f));
        }
        if (issuer.getPhone() != null && !issuer.getPhone().isBlank()) {
            leftCell.add(buildLabelValue("Téléphone : ", issuer.getPhone(), bold, regular, 10f));
        }

        leftCell.add(new Paragraph(" ").setFontSize(6f));

        if (issuer.getTpsNumber() != null && !issuer.getTpsNumber().isBlank()) {
            leftCell.add(buildLabelValue("TPS : ", issuer.getTpsNumber(), bold, regular, 10f));
        }
        if (issuer.getTvqNumber() != null && !issuer.getTvqNumber().isBlank()) {
            leftCell.add(buildLabelValue("TVQ : ", issuer.getTvqNumber(), bold, regular, 10f));
        }

        table.addCell(leftCell);

        // Colonne droite : client
        Cell rightCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(0);

        rightCell.add(new Paragraph("FACTURÉ À :")
                .setFont(regular).setFontSize(8f)
                .setFontColor(DARK_GRAY)
                .setMarginBottom(3f));

        rightCell.add(new Paragraph(client.getName())
                .setFont(bold).setFontSize(11f).setMarginBottom(2f));

        if (client.getAddress() != null && !client.getAddress().isBlank()) {
            rightCell.add(new Paragraph(client.getAddress())
                    .setFont(regular).setFontSize(10f).setMarginBottom(1f));
        }

        // Province + code postal
        StringBuilder line2 = new StringBuilder();
        if (client.getProvince() != null && !client.getProvince().isBlank())
            line2.append(client.getProvince());
        if (client.getPostalCode() != null && !client.getPostalCode().isBlank()) {
            if (!line2.isEmpty()) line2.append(" ");
            line2.append(client.getPostalCode());
        }
        if (!line2.isEmpty()) {
            rightCell.add(new Paragraph(line2.toString())
                    .setFont(regular).setFontSize(10f).setMarginBottom(1f));
        }

        if (client.getCountry() != null && !client.getCountry().isBlank()) {
            rightCell.add(new Paragraph(client.getCountry())
                    .setFont(regular).setFontSize(10f));
        }

        table.addCell(rightCell);
        doc.add(table);
    }

    private void addItemsTable(Document doc, Invoice invoice, PdfFont bold, PdfFont regular) {

        // 4 colonnes : Description | Temps | Prix Unit. | Montant
        float[] colWidths = {55f, 15f, 15f, 15f};
        Table table = new Table(UnitValue.createPercentArray(colWidths))
                .setWidth(UnitValue.createPercentValue(100));

        // ── En-têtes ──
        addHeaderCell(table, "Description",  bold, TextAlignment.LEFT);
        addHeaderCell(table, "Temps",        bold, TextAlignment.CENTER);
        addHeaderCell(table, "Prix Unit.",   bold, TextAlignment.CENTER);
        addHeaderCell(table, "Montant",      bold, TextAlignment.RIGHT);

        // ── Lignes de prestation ──
        for (InvoiceItem item : invoice.getItems()) {
            // Description (titre en gras + détail période dessous)
            Cell descCell = new Cell()
                    .setBorderTop(Border.NO_BORDER)
                    .setBorderLeft(Border.NO_BORDER)
                    .setBorderRight(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(LIGHT_GRAY, 0.5f))
                    .setPaddingTop(10f).setPaddingBottom(10f);

            descCell.add(new Paragraph(item.getDescription())
                    .setFont(bold).setFontSize(10f).setMarginBottom(2f));

            String period = item.getPeriodLabel();
            if (period != null && !period.isBlank()) {
                descCell.add(new Paragraph(period)
                        .setFont(regular).setFontSize(9f).setFontColor(DARK_GRAY));
            }
            table.addCell(descCell);

            // Temps
            String tempsLabel = formatQuantity(item.getQuantity()) + " " + item.getUnit();
            table.addCell(dataCell(tempsLabel, regular, TextAlignment.CENTER));

            // Prix unitaire
            String prixLabel = formatCurrency(item.getUnitPrice()) + " $/" + item.getUnit();
            table.addCell(dataCell(prixLabel, regular, TextAlignment.CENTER));

            // Montant
            table.addCell(dataCell(formatCurrency(item.getAmount()) + " $", regular, TextAlignment.RIGHT));
        }

        // ── Ligne séparatrice ──
        addSeparatorRow(table, 4);

        // ── TPS ──
        addTaxRow(table, "TPS 5%", invoice.getTpsAmount(), regular);

        // ── TVQ ──
        addTaxRow(table, "TVQ 9,975%", invoice.getTvqAmount(), regular);

        // ── TOTAL (bande noire) ──
        addTotalRow(table, invoice.getTotal(), bold);

        doc.add(table);
    }

    // ── Helpers cellules ─────────────────────────────────────────────────────

    private void addHeaderCell(Table table, String text, PdfFont font, TextAlignment align) {
        Cell cell = new Cell()
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(BLACK, 1.5f))
                .setPaddingBottom(6f)
                .setPaddingTop(4f);
        cell.add(new Paragraph(text).setFont(font).setFontSize(10f).setTextAlignment(align));
        table.addCell(cell);
    }

    private Cell dataCell(String text, PdfFont font, TextAlignment align) {
        Cell cell = new Cell()
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(LIGHT_GRAY, 0.5f))
                .setPaddingTop(10f).setPaddingBottom(10f);
        cell.add(new Paragraph(text).setFont(font).setFontSize(10f).setTextAlignment(align));
        return cell;
    }

    private void addSeparatorRow(Table table, int colspan) {
        Cell sep = new Cell(1, colspan)
                .setBorderTop(new SolidBorder(LIGHT_GRAY, 0.5f))
                .setBorderLeft(Border.NO_BORDER)
                .setBorderRight(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setPadding(0).setHeight(8f);
        table.addCell(sep);
    }

    private void addTaxRow(Table table, String label, BigDecimal amount, PdfFont regular) {
        // 3 premières colonnes fusionnées pour le label
        Cell labelCell = new Cell(1, 3)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(6f).setPaddingBottom(6f);
        labelCell.add(new Paragraph(label).setFont(regular).setFontSize(10f));
        table.addCell(labelCell);

        Cell amountCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(6f).setPaddingBottom(6f);
        amountCell.add(new Paragraph(formatCurrency(amount) + " $")
                .setFont(regular).setFontSize(10f).setTextAlignment(TextAlignment.RIGHT));
        table.addCell(amountCell);
    }

    private void addTotalRow(Table table, BigDecimal total, PdfFont bold) {
        // Label TOTAL
        Cell labelCell = new Cell(1, 3)
                .setBackgroundColor(BLACK)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10f).setPaddingBottom(10f).setPaddingLeft(6f);
        labelCell.add(new Paragraph("TOTAL")
                .setFont(bold).setFontSize(12f)
                .setFontColor(ColorConstants.WHITE));
        table.addCell(labelCell);

        // Montant total
        Cell amountCell = new Cell()
                .setBackgroundColor(BLACK)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10f).setPaddingBottom(10f).setPaddingRight(6f);
        amountCell.add(new Paragraph(formatCurrency(total) + " $")
                .setFont(bold).setFontSize(12f)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(amountCell);
    }

    // ── Helpers texte ─────────────────────────────────────────────────────────

    /** Construit un paragraphe "Label :" + valeur en regular */
    private Paragraph buildLabelValue(String label, String value,
                                      PdfFont bold, PdfFont regular, float size) {
        return new Paragraph()
                .add(new Text(label).setFont(bold).setFontSize(size))
                .add(new Text(value).setFont(regular).setFontSize(size))
                .setMarginBottom(2f);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0,00";
        return CURRENCY_FORMAT.format(amount);
    }

    private String formatQuantity(BigDecimal qty) {
        if (qty == null) return "0";
        // Supprime les zéros inutiles (24.50 → 24,5)
        return CURRENCY_FORMAT.format(qty.stripTrailingZeros());
    }
}
