import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-charts',
  templateUrl: './charts.component.html',
  styleUrls: ['./charts.component.css']
})
export class ChartsComponent implements OnInit {
  chart1Data: SafeResourceUrl | null = null;
  chart2Data: SafeResourceUrl | null = null;
  chartForm: FormGroup;
  userId: any;

  constructor(
    private http: HttpClient,
    private sanitizer: DomSanitizer,
    private formBuilder: FormBuilder
  ) {
    this.chartForm = this.formBuilder.group({
      startTime: [null],
      endTime: [null]
    });
  }

  ngOnInit(): void {
    this.userId = localStorage.getItem('userId');
    if (this.userId) {
      this.fetchChart1Data(this.userId);
      this.fetchChart2Data(this.userId);
    }
  }

  onStartDateChange(date: string) {
    if (date) {
      // Append the start time to the selected date
      const startTime = 'T00:00';
      const startDateWithTime = date + startTime;

      // Set the updated value to the form control or any variable you're using
      this.chartForm.controls['startTime'].setValue(startDateWithTime);
    } else {
      // Clear the start time value
      this.chartForm.controls['startTime'].setValue(null); // Or set to an empty string: ''
    }
  }

  onEndDateChange(date: string) {
    if (date) {
      // Append the end time to the selected date
      const endTime = 'T23:59';
      const endDateWithTime = date + endTime;

      // Set the updated value to the form control or any variable you're using
      this.chartForm.controls['endTime'].setValue(endDateWithTime);
    } else {
      // Clear the end time value
      this.chartForm.controls['endTime'].setValue(null); // Or set to an empty string: ''
    }
  }

  fetchChart1Data(userId: string): void {
    const requestData = {
      userId: parseInt(userId),
      startTime: this.chartForm.get('startTime')?.value,
      endTime: this.chartForm.get('endTime')?.value
    };

    this.http.post('http://localhost:8080/api/feed/milkChart', requestData, { responseType: 'text' })
      .subscribe(data => {
        // Decode the base64 image data and sanitize the URL
        const imageUrl = 'data:image/png;base64,' + data;
        this.chart1Data = this.sanitizer.bypassSecurityTrustResourceUrl(imageUrl);
      });
  }

  fetchChart2Data(userId: string): void {
    const requestData = {
      userId: parseInt(userId),
      startTime: this.chartForm.get('startTime')?.value,
      endTime: this.chartForm.get('endTime')?.value
    };

    this.http.post('http://localhost:8080/api/feed/durationChart', requestData, { responseType: 'text' })
      .subscribe(data => {
        // Decode the base64 image data and sanitize the URL
        const imageUrl = 'data:image/png;base64,' + data;
        this.chart2Data = this.sanitizer.bypassSecurityTrustResourceUrl(imageUrl);
      });
  }
}
