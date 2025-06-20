package com.donorapi.report.util;


import com.donorapi.report.model.HospitalReportDTO;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;




/**
 * Utility class for exporting reports to Excel format
 */
@Component
@Log4j2
public class ExcelExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Export a hospital report to Excel
     * @param report The hospital report data
     * @return The Excel file as a byte array
     */
    public byte[] exportHospitalReportToExcel(HospitalReportDTO report) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // Create a Summary sheet
            Sheet summarySheet = workbook.createSheet("Summary");
            
            // Add title
            Row titleRow = summarySheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Hospital Donation Report");
            
            // Add hospital info
            Row hospitalRow = summarySheet.createRow(1);
            Cell hospitalCell = hospitalRow.createCell(0);
            hospitalCell.setCellValue("Hospital: " + report.getHospitalName());
            
            // Add a report period
            Row periodRow = summarySheet.createRow(2);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue("Period: " + report.getReportPeriod());
            
            // Add summary data
            Row summaryHeaderRow = summarySheet.createRow(4);
            createHeaderRow(summaryHeaderRow, headerStyle, "Metric", "Value");
            
            int rowNum = 5;
            createDataRow(summarySheet.createRow(rowNum++), dataStyle, "Total Donations", String.valueOf(report.getTotalDonations()));
            createDataRow(summarySheet.createRow(rowNum++), dataStyle, "Total Volume (ml)", String.format("%.2f", report.getTotalVolumeMl()));
            createDataRow(summarySheet.createRow(rowNum++), dataStyle, "Unique Donors", String.valueOf(report.getUniqueDonors()));
            createDataRow(summarySheet.createRow(rowNum++), dataStyle, "Date Range", 
                    report.getStartDate().format(DATE_FORMATTER) + " - " + 
                    report.getEndDate().format(DATE_FORMATTER));
            
            // Auto-size columns
            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);
            
            // Create a Quarterly Data sheet if available
            if (report.getQuarterlyData() != null && !report.getQuarterlyData().isEmpty()) {
                Sheet quarterlySheet = workbook.createSheet("Quarterly Data");
                
                // Add header
                Row headerRow = quarterlySheet.createRow(0);
                createHeaderRow(headerRow, headerStyle, "Quarter", "Donations", "Volume (ml)", "Donors");
                
                // Add data rows
                rowNum = 1;
                for (HospitalReportDTO.QuarterlyData data : report.getQuarterlyData()) {
                    Row row = quarterlySheet.createRow(rowNum++);
                    int colNum = 0;
                    row.createCell(colNum++).setCellValue(data.getQuarter());
                    row.createCell(colNum++).setCellValue(data.getDonations());
                    row.createCell(colNum++).setCellValue(data.getVolumeMl());
                    row.createCell(colNum++).setCellValue(data.getDonors());
                    
                    // Apply style
                    for (int i = 0; i < colNum; i++) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
                
                // Auto-size columns
                for (int i = 0; i < 4; i++) {
                    quarterlySheet.autoSizeColumn(i);
                }
            }
            
            // Create a Blood Type Distribution sheet if available
            if (report.getBloodTypeDistribution() != null && !report.getBloodTypeDistribution().isEmpty()) {
                Sheet bloodTypeSheet = workbook.createSheet("Blood Type Distribution");
                
                // Add header
                Row headerRow = bloodTypeSheet.createRow(0);
                createHeaderRow(headerRow, headerStyle, "Blood Type", "Count", "Percentage");
                
                // Add data rows
                rowNum = 1;
                for (HospitalReportDTO.BloodTypeData data : report.getBloodTypeDistribution()) {
                    Row row = bloodTypeSheet.createRow(rowNum++);
                    int colNum = 0;
                    row.createCell(colNum++).setCellValue(data.getBloodType());
                    row.createCell(colNum++).setCellValue(data.getCount());
                    row.createCell(colNum++).setCellValue(String.format("%.2f%%", data.getPercentage()));
                    
                    // Apply style
                    for (int i = 0; i < colNum; i++) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
                
                // Auto-size columns
                for (int i = 0; i < 3; i++) {
                    bloodTypeSheet.autoSizeColumn(i);
                }
            }
            
            // Write to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error exporting hospital report to Excel", e);
            throw new RuntimeException("Error exporting hospital report to Excel", e);
        }
    }

    
    // Helper methods
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        return style;
    }
    
    private void createHeaderRow(Row row, CellStyle style, String... headers) {
        int colNum = 0;
        for (String header : headers) {
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(header);
            cell.setCellStyle(style);
        }
    }
    
    private void createDataRow(Row row, CellStyle style, String label, String value) {
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(style);
    }
}