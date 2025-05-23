import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-hospital-report',
  templateUrl: './hospital-report.component.html',
  styleUrls: ['./hospital-report.component.css']
})
export class HospitalReportComponent implements OnInit {
  reportForm: FormGroup;
  reportData: any = null;
  loading = false;
  error = '';
  years: number[] = [];
  quarters: number[] = [1, 2, 3, 4];
  
  constructor(
    private fb: FormBuilder,
    private http: HttpClient
  ) {
    // Generate years (current year and 5 years back)
    const currentYear = new Date().getFullYear();
    for (let i = 0; i < 6; i++) {
      this.years.push(currentYear - i);
    }
  }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.reportForm = this.fb.group({
      reportType: ['QUARTERLY', Validators.required],
      reportFormat: ['JSON', Validators.required],
      year: [new Date().getFullYear(), Validators.required],
      quarter: [this.getCurrentQuarter(), Validators.required],
      yearOnly: [new Date().getFullYear(), Validators.required],
      startDate: [null],
      endDate: [null],
      includeBloodTypeDistribution: [true],
      includeHospitalComparison: [false]
    });

    // Add validators based on report type
    this.reportForm.get('reportType').valueChanges.subscribe(reportType => {
      this.updateValidators(reportType);
    });
  }

  updateValidators(reportType: string): void {
    const yearControl = this.reportForm.get('year');
    const quarterControl = this.reportForm.get('quarter');
    const yearOnlyControl = this.reportForm.get('yearOnly');
    const startDateControl = this.reportForm.get('startDate');
    const endDateControl = this.reportForm.get('endDate');

    // Reset validators
    yearControl.clearValidators();
    quarterControl.clearValidators();
    yearOnlyControl.clearValidators();
    startDateControl.clearValidators();
    endDateControl.clearValidators();

    // Add validators based on report type
    if (reportType === 'QUARTERLY') {
      yearControl.setValidators(Validators.required);
      quarterControl.setValidators(Validators.required);
    } else if (reportType === 'YEARLY') {
      yearOnlyControl.setValidators(Validators.required);
    } else if (reportType === 'CUSTOM') {
      startDateControl.setValidators(Validators.required);
      endDateControl.setValidators(Validators.required);
    }

    // Update validators
    yearControl.updateValueAndValidity();
    quarterControl.updateValueAndValidity();
    yearOnlyControl.updateValueAndValidity();
    startDateControl.updateValueAndValidity();
    endDateControl.updateValueAndValidity();
  }

  getCurrentQuarter(): number {
    const month = new Date().getMonth();
    return Math.floor(month / 3) + 1;
  }

  generateReport(): void {
    if (this.reportForm.invalid) {
      this.error = 'Please fill all required fields';
      return;
    }

    this.loading = true;
    this.error = '';
    
    // Get hospital ID from user context or route parameter
    const hospitalId = 1; // Replace with actual hospital ID
    
    const requestBody = this.prepareRequestBody();
    
    this.http.post<any>(`/api/reports/hospital/${hospitalId}`, requestBody)
      .subscribe(
        data => {
          this.reportData = data;
          this.loading = false;
        },
        error => {
          this.error = 'Error generating report: ' + (error.error?.message || error.message || 'Unknown error');
          this.loading = false;
        }
      );
  }

  exportReport(format: string): void {
    if (this.reportForm.invalid) {
      this.error = 'Please fill all required fields';
      return;
    }

    this.loading = true;
    this.error = '';
    
    // Get hospital ID from user context or route parameter
    const hospitalId = 1; // Replace with actual hospital ID
    
    const requestBody = this.prepareRequestBody();
    requestBody.reportFormat = format;
    
    this.http.post(`/api/reports/hospital/${hospitalId}/export/${format.toLowerCase()}`, requestBody, {
      responseType: 'blob',
      observe: 'response'
    }).subscribe(
      response => {
        this.loading = false;
        
        // Get filename from content-disposition header or use default
        let filename = 'hospital_report.' + (format === 'PDF' ? 'pdf' : 'xlsx');
        const contentDisposition = response.headers.get('content-disposition');
        if (contentDisposition) {
          const matches = /filename="?([^"]*)"?/.exec(contentDisposition);
          if (matches && matches[1]) {
            filename = matches[1];
          }
        }
        
        // Save file
        saveAs(response.body, filename);
      },
      error => {
        this.error = 'Error exporting report: ' + (error.error?.message || error.message || 'Unknown error');
        this.loading = false;
      }
    );
  }

  prepareRequestBody(): any {
    const formValue = this.reportForm.value;
    const requestBody: any = {
      reportType: formValue.reportType,
      reportFormat: formValue.reportFormat,
      includeBloodTypeDistribution: formValue.includeBloodTypeDistribution,
      includeHospitalComparison: formValue.includeHospitalComparison
    };
    
    if (formValue.reportType === 'QUARTERLY') {
      requestBody.year = formValue.year;
      requestBody.quarter = formValue.quarter;
    } else if (formValue.reportType === 'YEARLY') {
      requestBody.yearOnly = formValue.yearOnly;
    } else if (formValue.reportType === 'CUSTOM') {
      requestBody.startDate = formValue.startDate;
      requestBody.endDate = formValue.endDate;
    }
    
    return requestBody;
  }
}