# Reporting Functionality Documentation

This document provides an overview of the reporting functionality implemented in the Blood Donation System. The reporting functionality allows hospitals and donors to generate and export reports about donations, appointments, and other relevant metrics.

## Backend Endpoints

### Hospital Reports

#### Generate Hospital Report
- **URL**: `/api/reports/hospital/{hospitalId}`
- **Method**: `POST`
- **Auth Required**: Yes (HOSPITAL role)
- **Request Body**: `ReportRequest` (see model below)
- **Response**: `HospitalReportDTO` (see model below)
- **Description**: Generates a report for a specific hospital with various metrics including total donations, total volume, unique donors, blood type distribution, and quarterly breakdown.

#### Export Hospital Report as PDF
- **URL**: `/api/reports/hospital/{hospitalId}/export/pdf`
- **Method**: `POST`
- **Auth Required**: Yes (HOSPITAL role)
- **Request Body**: `ReportRequest` (see model below)
- **Response**: PDF file
- **Description**: Generates and exports a hospital report as a PDF file.

#### Export Hospital Report as Excel
- **URL**: `/api/reports/hospital/{hospitalId}/export/excel`
- **Method**: `POST`
- **Auth Required**: Yes (HOSPITAL role)
- **Request Body**: `ReportRequest` (see model below)
- **Response**: Excel file
- **Description**: Generates and exports a hospital report as an Excel file.

#### Generate Hospital Comparison Report
- **URL**: `/api/reports/hospital/comparison`
- **Method**: `POST`
- **Auth Required**: Yes (ADMIN role)
- **Request Body**: `ReportRequest` (see model below)
- **Response**: List of `HospitalReportDTO` (see model below)
- **Description**: Generates a comparison report for all hospitals, sorted by total donations (descending).

### Donor Reports

#### Generate Donor Report
- **URL**: `/api/reports/donor/{donorId}`
- **Method**: `POST`
- **Auth Required**: Yes (DONOR role)
- **Request Body**: `ReportRequest` (see model below)
- **Response**: `DonorReportDTO` (see model below)
- **Description**: Generates a report for a specific donor with various metrics including total donations, total volume, appointment statistics, and hospital breakdown.

#### Export Donor Report as PDF
- **URL**: `/api/reports/donor/{donorId}/export/pdf`
- **Method**: `POST`
- **Auth Required**: Yes (DONOR role)
- **Request Body**: `ReportRequest` (see model below)
- **Response**: PDF file
- **Description**: Generates and exports a donor report as a PDF file.

#### Export Donor Report as Excel
- **URL**: `/api/reports/donor/{donorId}/export/excel`
- **Method**: `POST`
- **Auth Required**: Yes (DONOR role)
- **Request Body**: `ReportRequest` (see model below)
- **Response**: Excel file
- **Description**: Generates and exports a donor report as an Excel file.

## Models

### ReportRequest

```json
{
  "reportType": "QUARTERLY",
  "reportFormat": "JSON",

  "year": 2023,
  "quarter": 1,

  "yearOnly": 2023,

  "startDate": "2023-01-01",
  "endDate": "2023-03-31",

  "includeBloodTypeDistribution": true,
  "includeHospitalComparison": false,
  "includeAppointmentStats": true,
  "includeRequestStats": true,
  "includeHospitalBreakdown": true
}
```

Notes:
- `reportType` can be: QUARTERLY, YEARLY, CUSTOM, ALL_TIME
- `reportFormat` can be: JSON, PDF, EXCEL
- For QUARTERLY reports, use `year` and `quarter`
- For YEARLY reports, use `yearOnly`
- For CUSTOM reports, use `startDate` and `endDate`
```

### HospitalReportDTO

```json
{
  "hospitalId": 1,
  "hospitalName": "General Hospital",
  "reportPeriod": "Q1 2023",
  "startDate": "2023-01-01",
  "endDate": "2023-03-31",
  "totalDonations": 150,
  "totalVolumeMl": 67500.0,
  "uniqueDonors": 120,

  "quarterlyData": [
    {
      "quarter": "Q1",
      "donations": 50,
      "volumeMl": 22500.0,
      "donors": 45
    }
  ],

  "bloodTypeDistribution": [
    {
      "bloodType": "A+",
      "count": 60,
      "percentage": 40.0
    }
  ]
}
```

Note: The `quarterlyData` and `bloodTypeDistribution` arrays can contain multiple items.
```

### DonorReportDTO

```json
{
  "donorId": 1,
  "donorName": "John Doe",
  "bloodType": "A+",
  "reportPeriod": "Q1 2023",
  "startDate": "2023-01-01",
  "endDate": "2023-03-31",

  "totalDonations": 3,
  "totalVolumeMl": 1350.0,
  "firstDonationDate": "2023-01-15",
  "lastDonationDate": "2023-03-20",

  "totalAppointments": 4,
  "completedAppointments": 3,
  "scheduledAppointments": 1,
  "expiredAppointments": 0,
  "cancelledAppointments": 0,

  "periodData": [
    {
      "period": "Q1 2023",
      "donations": 3,
      "volumeMl": 1350.0
    }
  ],

  "hospitalData": [
    {
      "hospitalId": 1,
      "hospitalName": "General Hospital",
      "donations": 2,
      "volumeMl": 900.0,
      "percentage": 66.67
    }
  ]
}
```

Note: The `periodData` and `hospitalData` arrays can contain multiple items.
```

## Frontend Components

### Angular Component for Hospital Reports

The hospital report component provides a user interface for hospitals to generate and export reports. It includes:

- A form for selecting report parameters (report type, format, date range, etc.)
- Buttons for generating and exporting reports
- A results section that displays the report data

To use the component:

1. Add the component to your Angular module:
   ```typescript
   import { HospitalReportComponent } from './hospital-report.component';

   @NgModule({
     declarations: [
       // Other components...
       HospitalReportComponent
     ],
     // Other module configuration...
   })
   export class AppModule { }
   ```

2. Add the component to your template:
   ```html
   <app-hospital-report></app-hospital-report>
   ```

### Flutter Screen for Donor Reports

The donor report screen provides a user interface for donors to generate and export reports. It includes:

- A form for selecting report parameters (report type, format, date range, etc.)
- Buttons for generating and exporting reports
- A results section that displays the report data

To use the screen:

1. Add the screen to your Flutter app:
   ```dart
   import 'package:your_app/donor_report_screen.dart';

   // Example of how to navigate to the screen from a button press
   void navigateToDonorReport(BuildContext context) {
     Navigator.push(
       context,
       MaterialPageRoute(builder: (context) => DonorReportScreen()),
     );
   }
   ```

## Implementation Details

### Backend

The reporting functionality is implemented using the following components:

1. **Models**: Data Transfer Objects (DTOs) for report requests and responses
2. **Service**: ReportService interface and ReportServiceImpl implementation
3. **Controller**: ReportController for exposing the API endpoints
4. **Utilities**: PdfExporter and ExcelExporter for exporting reports

The implementation follows these steps:

1. The controller receives a report request and validates it
2. The service generates the report data based on the request parameters
3. The service queries the database for the relevant data (donations, appointments, etc.)
4. The service aggregates the data and calculates the metrics
5. The service returns the report data or exports it as a file

### Frontend

The frontend components are implemented using:

1. **Angular**: For the web interface
2. **Flutter**: For the mobile interface

Both implementations follow a similar pattern:

1. Provide a form for selecting report parameters
2. Validate the form input
3. Send a request to the backend API
4. Display the report data or download the exported file

## Best Practices

1. **Caching**: Consider caching report results to improve performance for frequently accessed reports
2. **Pagination**: For large reports, implement pagination to avoid loading too much data at once
3. **Security**: Ensure that users can only access reports for their own data or data they are authorized to see
4. **Error Handling**: Provide clear error messages and handle edge cases gracefully
5. **Responsive Design**: Ensure that the frontend components work well on different screen sizes
