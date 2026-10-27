package br.com.rodrigodip.invoicing.PdfRender;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.invoicing.dto.InvoiceRepresentation;
import br.com.rodrigodip.invoicing.exceptions.InvoiceRenderException;

@Component
public class OpenPdfInvoiceRenderer {

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
    private static final Font H2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font BODY = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font TH = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font TOTAL = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

    private static final Locale PT_BR = Locale.of("pt", "BR");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] render(InvoiceRepresentation e) {

        NumberFormat brl = NumberFormat.getCurrencyInstance(PT_BR);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            try {
                PdfWriter.getInstance(doc, out);
                doc.open();

                addHeader(doc, e);
                addCustomer(doc, e);
                addItems(doc, e, brl);
                addTotal(doc, e, brl);
            } finally {
                if (doc.isOpen()) {
                    doc.close();
                }
            }

            return out.toByteArray();
        } catch (DocumentException | IOException ex) {
            throw new InvoiceRenderException("Failed to render invoice for order " + e.orderId(), ex);
        }
    }

    private void addItems(Document doc, InvoiceRepresentation e, NumberFormat brl) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[] { 5f, 1.5f, 2.5f, 2.5f });
        table.setWidthPercentage(100);

        for (String h : List.of("Product", "Qty", "Unit price", "Total")) {
            PdfPCell cell = new PdfPCell(new Phrase(h, TH));
            cell.setBackgroundColor(new Color(50, 50, 50));
            cell.setPadding(6);
            table.addCell(cell);
        }

        List<InvoiceRepresentation.Item> items = e.itensList() == null ? List.of() : e.itensList();
        for (InvoiceRepresentation.Item item : items) {
            table.addCell(cell(item.name(), Element.ALIGN_LEFT));
            table.addCell(cell(String.valueOf(item.quantity()), Element.ALIGN_CENTER));
            table.addCell(cell(brl.format(item.unitPrice()), Element.ALIGN_RIGHT));
            table.addCell(cell(brl.format(item.lineTotal()), Element.ALIGN_RIGHT));
        }
        doc.add(table);
    }

    private void addTotal(Document doc, InvoiceRepresentation e, NumberFormat brl) throws DocumentException {
        BigDecimal totalValue = e.total() == null ? BigDecimal.ZERO : e.total();
        Paragraph total = new Paragraph("Total: " + brl.format(totalValue), TOTAL);
        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(12);
        doc.add(total);
    }

    private void addHeader(Document doc, InvoiceRepresentation e) throws DocumentException {
        doc.add(new Paragraph("INVOICE", TITLE));
        doc.add(new Paragraph("Invoice no.: " + String.format("INV-%06d", e.orderId()), BODY));
        doc.add(new Paragraph("Order: #" + e.orderId(), BODY));
        doc.add(new Paragraph("Issue date: " + LocalDate.now().format(DATE), BODY));
        doc.add(spacer());
    }

    private void addCustomer(Document doc, InvoiceRepresentation e) throws DocumentException {
        doc.add(new Paragraph("Customer", H2));
        doc.add(new Paragraph(safe(e.clientName()), BODY));
        doc.add(new Paragraph("CPF: " + safe(formatCpf(e.clientCpf())), BODY));
        doc.add(new Paragraph(safe(e.clientAddress()) + ", " + safe(e.clientAddressNumber())
                + " - ZIP " + safe(e.clientZipcode()), BODY));
        doc.add(new Paragraph(safe(e.clientEmail()) + " | " + safe(e.clientPhone()), BODY));
        doc.add(spacer());
    }

    private PdfPCell cell(String text, int align) {
        PdfPCell c = new PdfPCell(new Phrase(safe(text), BODY));
        c.setHorizontalAlignment(align);
        c.setPadding(6);
        return c;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private Paragraph spacer() {
        return new Paragraph(" ", BODY);
    }

    private String formatCpf(String cpf) {
        if (cpf == null || cpf.length() != 11)
            return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}
