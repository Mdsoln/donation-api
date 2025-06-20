package com.donorapi.report.util;

import com.donorapi.report.model.DonorReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
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
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            // --- Fonts ---
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);
            Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.RED);
            Font keyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font boldKeyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);

            // --- Title and Subtitle Centered ---
            Paragraph title = new Paragraph("Donor Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            Paragraph donorInfo = new Paragraph(
                    "Donor: " + report.getDonorName() + "\n" +
                            "Blood Group: " + (report.getBloodType() != null ? report.getBloodType() : "N/A") + "\n" +
                            "Location: " + (report.getLocation() != null ? report.getLocation() : "N/A"),
                    subtitleFont
            );
            donorInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(donorInfo);

            document.add(Chunk.NEWLINE);

            // --- Section: Donation Summary ---
            document.add(new Paragraph("Donation Summary", sectionTitleFont));
            document.add(makeKeyValueTable(
                    "Total Donation", String.valueOf(report.getTotalDonations()),
                    "Last Donation", formatDate(report.getLastDonation(), report.getLocation()),
                    "Eligible Date", format(report.getEligibleDate())
            ));
            document.add(Chunk.NEWLINE);

            // --- Section: Appointments ---
            document.add(new Paragraph("Appointments", sectionTitleFont));
            document.add(makeKeyValueTable(
                    "Appointment Booked", String.valueOf(report.getScheduledAppointments()),
                    "Appointments Attended", String.valueOf(report.getCompletedAppointments()),
                    "Appointments Missed", String.valueOf(report.getExpiredAppointments())
            ));
            document.add(Chunk.NEWLINE);

            // --- Section: Urgent Requests ---
//            document.add(new Paragraph("Urgent Requests", sectionTitleFont));
//            document.add(makeKeyValueTable(
//                    "Request Received", String.valueOf(report.getTotalRequests()),
//                    "Responded", String.valueOf(report.getTotalResponded())
//            ));
//            document.add(Chunk.NEWLINE);

            // --- Section: Top Donation Center ---
            document.add(new Paragraph("Top Donation Center", sectionTitleFont));
            document.add(new Paragraph(report.getTopDonatingCenter() != null ? report.getTopDonatingCenter() : "N/A", keyFont));
            document.add(Chunk.NEWLINE);

            // --- Section: Most Active Month ---
            document.add(new Paragraph("Most Active Month", sectionTitleFont));
            document.add(new Paragraph(report.getActiveMonth() != null ? report.getActiveMonth() : "N/A", keyFont));

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // --- Footer ---
            Paragraph footer = new Paragraph("All copyrights are reserved", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error exporting donor report to PDF", e);
            throw new RuntimeException("Error exporting donor report to PDF", e);
        }
    }

    private String format(LocalDate date) {
        return (date != null) ? date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A";
    }


    private PdfPTable makeKeyValueTable(String key1, String val1, String key2, String val2, String key3, String val3) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(5f);

        table.addCell(new Phrase(key1 + ":", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
        table.addCell(new Phrase(val1, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

        table.addCell(new Phrase(key2 + ":", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
        table.addCell(new Phrase(val2, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

        table.addCell(new Phrase(key3 + ":", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
        table.addCell(new Phrase(val3, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

        return table;
    }

    private PdfPTable makeKeyValueTable(String key1, String val1, String key2, String val2) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(90);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(5f);

        table.addCell(new Phrase(key1 + ":", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
        table.addCell(new Phrase(val1, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

        table.addCell(new Phrase(key2 + ":", new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL)));
        table.addCell(new Phrase(val2, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));

        return table;
    }

    private String formatDate(LocalDate date, String location) {
        if (date == null) return "N/A";
        String formatted = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        return location != null ? formatted + ", " + location : formatted;
    }

}