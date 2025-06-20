package com.donorapi.report.util;

import com.donorapi.report.model.DonorReportDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;



/**
 * Utility class for exporting reports to PDF format
 */
@Component
@Log4j2
public class PdfExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Export a hospital report to PDF
     * @param report The hospital report data
     * @return The PDF file as a byte array
     */
    public byte[] exportDonorReportToPdf(DonorReportDTO report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
            Document doc = new Document(pdfDoc);

            // Fonts
            var bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            var regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Title
            Paragraph title = new Paragraph("Donor Report")
                    .setFont(bold)
                    .setFontSize(22)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                    .setMarginBottom(20);
            doc.add(title);

            // Donor Info
            doc.add(new Paragraph("Donor: " + report.getDonorName())
                    .setFont(bold).setFontSize(16).setTextAlignment((com.itextpdf.layout.properties.TextAlignment.CENTER)));
            doc.add(new Paragraph("Blood Group: " + (report.getBloodType() != null ? report.getBloodType() : "N/A"))
                    .setFont(bold).setFontSize(16).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            doc.add(new Paragraph("Location: " + (report.getLocation() != null ? report.getLocation() : "N/A"))
                    .setFont(bold).setFontSize(16).setFontColor(ColorConstants.BLUE).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));

            // Donation Summary
            doc.add(new Paragraph("Donation Summary")
                    .setFont(bold).setFontSize(18).setFontColor(ColorConstants.RED).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));
            doc.add(centeredKeyValue("Total Donation", String.valueOf(report.getTotalDonations()), regular, bold));
            doc.add(centeredKeyValue("Last Donation", report.getLastDonation() != null ? report.getLastDonation().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A", regular, bold));
            doc.add(centeredKeyValue("Eligible Date", report.getEligibleDate() != null ? report.getEligibleDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A", regular, bold));
            doc.add(new Paragraph("\n"));

            // Appointments
            doc.add(new Paragraph("Appointments")
                    .setFont(bold).setFontSize(18).setFontColor(ColorConstants.RED).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));
            doc.add(centeredKeyValue("Appointments Booked", String.valueOf(report.getScheduledAppointments()), regular, bold));
            doc.add(centeredKeyValue("Appointments Attended", String.valueOf(report.getCompletedAppointments()), regular, bold));
            doc.add(centeredKeyValue("Appointments Missed", String.valueOf(report.getExpiredAppointments()), regular, bold));
            doc.add(new Paragraph("\n"));

            // Top Donation Center
            doc.add(new Paragraph("Top Donation Center")
                    .setFont(bold).setFontSize(18).setFontColor(ColorConstants.RED).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));
            doc.add(centeredKeyValue("Center", report.getTopDonatingCenter() != null ? report.getTopDonatingCenter() : "N/A", regular, bold));
            doc.add(new Paragraph("\n"));

            // Most Active Month
            doc.add(new Paragraph("Most Active Month")
                    .setFont(bold).setFontSize(18).setFontColor(ColorConstants.RED).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));
            doc.add(centeredKeyValue("Month", report.getActiveMonth() != null ? report.getActiveMonth() : "N/A", regular, bold));
            doc.add(new Paragraph("\n\n"));

            // Footer
            doc.add(new Paragraph("All copyrights are reserved")
                    .setFont(regular).setFontSize(12).setFontColor(ColorConstants.GRAY).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error exporting donor report to PDF", e);
        }
    }


    private Paragraph centeredKeyValue(String key, String value, com.itextpdf.kernel.font.PdfFont regular, com.itextpdf.kernel.font.PdfFont bold) {
        return new Paragraph()
                .add(new com.itextpdf.layout.element.Text(key + ": ").setFont(bold).setFontSize(15))
                .add(new com.itextpdf.layout.element.Text(value).setFont(regular).setFontSize(15))
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(5);
    }

}