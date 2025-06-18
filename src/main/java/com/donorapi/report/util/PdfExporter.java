package com.donorapi.report.util;

import com.donorapi.report.model.DonorReportDTO;
import com.donorapi.report.model.HospitalReportDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
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

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font SECTION_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Export a hospital report to PDF
     * @param report The hospital report data
     * @return The PDF file as a byte array
     */
    public byte[] exportHospitalReportToPdf(HospitalReportDTO report) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Add title
            Paragraph title = new Paragraph("Hospital Donation Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Add hospital info
            Paragraph hospitalInfo = new Paragraph("Hospital: " + report.getHospitalName(), SUBTITLE_FONT);
            hospitalInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(hospitalInfo);
            
            // Add report period
            Paragraph period = new Paragraph("Period: " + report.getReportPeriod(), SUBTITLE_FONT);
            period.setAlignment(Element.ALIGN_CENTER);
            document.add(period);
            
            document.add(Chunk.NEWLINE);
            
            // Add summary section
            document.add(new Paragraph("Summary", SECTION_FONT));
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            
            addTableRow(summaryTable, "Total Donations:", String.valueOf(report.getTotalDonations()));
            addTableRow(summaryTable, "Total Volume:", String.format("%.2f ml", report.getTotalVolumeMl()));
            addTableRow(summaryTable, "Unique Donors:", String.valueOf(report.getUniqueDonors()));
            addTableRow(summaryTable, "Date Range:", 
                    report.getStartDate().format(DATE_FORMATTER) + " - " + 
                    report.getEndDate().format(DATE_FORMATTER));
            
            document.add(summaryTable);
            document.add(Chunk.NEWLINE);
            
            // Add quarterly data if available
            if (report.getQuarterlyData() != null && !report.getQuarterlyData().isEmpty()) {
                document.add(new Paragraph("Quarterly Breakdown", SECTION_FONT));
                PdfPTable quarterlyTable = new PdfPTable(4);
                quarterlyTable.setWidthPercentage(100);
                
                // Add header
                addTableHeader(quarterlyTable, "Quarter", "Donations", "Volume (ml)", "Donors");
                
                // Add data rows
                for (HospitalReportDTO.QuarterlyData data : report.getQuarterlyData()) {
                    addTableRow(quarterlyTable, 
                            data.getQuarter(), 
                            String.valueOf(data.getDonations()), 
                            String.format("%.2f", data.getVolumeMl()), 
                            String.valueOf(data.getDonors()));
                }
                
                document.add(quarterlyTable);
                document.add(Chunk.NEWLINE);
            }
            
            // Add blood type distribution if available
            if (report.getBloodTypeDistribution() != null && !report.getBloodTypeDistribution().isEmpty()) {
                document.add(new Paragraph("Blood Type Distribution", SECTION_FONT));
                PdfPTable bloodTypeTable = new PdfPTable(3);
                bloodTypeTable.setWidthPercentage(100);
                
                // Add header
                addTableHeader(bloodTypeTable, "Blood Type", "Count", "Percentage");
                
                // Add data rows
                for (HospitalReportDTO.BloodTypeData data : report.getBloodTypeDistribution()) {
                    addTableRow(bloodTypeTable, 
                            data.getBloodType(), 
                            String.valueOf(data.getCount()), 
                            String.format("%.2f%%", data.getPercentage()));
                }
                
                document.add(bloodTypeTable);
            }
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error exporting hospital report to PDF", e);
            throw new RuntimeException("Error exporting hospital report to PDF", e);
        }
    }
    
    /**
     * Export a donor report to PDF
     * @param report The donor report data
     * @return The PDF file as a byte array
     */
    public byte[] exportDonorReportToPdf(DonorReportDTO report) {
        try {
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            
            document.open();

            Font sectionRedFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.RED);
            // Add title
            Paragraph title = new Paragraph("Donor Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Add donor info
            Paragraph header = new Paragraph();
            header.setAlignment(Element.ALIGN_CENTER);
            header.add(new Paragraph("Donor: " + report.getDonorName(), NORMAL_FONT));
            header.add(new Paragraph("Blood Group: " + report.getBloodType(), NORMAL_FONT));
            header.add(new Paragraph("Location: " + report.getLocation(), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE)));
            document.add(header);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Donation Summary", sectionRedFont));
            document.add(new Paragraph("Total Donations: " + report.getTotalDonations(), NORMAL_FONT));
            document.add(new Paragraph("Last Donation" + report.getLastDonation(), NORMAL_FONT));
            document.add(new Paragraph("Eligible Date: " + report.getEligibleDate().format(DATE_FORMATTER), NORMAL_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable donationTable = new PdfPTable(2);
            donationTable.setWidthPercentage(100);
            donationTable.getDefaultCell().setPadding(8); // mimic inner padding
            donationTable.getDefaultCell().setBorder(Rectangle.BOX);

            document.add(new Paragraph("Top Donation Center", sectionRedFont));
            //document.add(new Paragraph(report.getTopDonationCenter(), NORMAL_FONT));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Most Active Month", sectionRedFont));
            //document.add(new Paragraph(report.getMostActiveMonth(), NORMAL_FONT));
            document.add(Chunk.NEWLINE);

            Paragraph footer = new Paragraph("All copyrights are reserved",
                    new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            document.add(Chunk.NEWLINE);

            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error exporting donor report to PDF", e);
            throw new RuntimeException("Error exporting donor report to PDF", e);
        }
    }
    
    // Helper methods
    
    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, SECTION_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }
    
    private void addTableRow(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, NORMAL_FONT));
            table.addCell(cell);
        }
    }
}