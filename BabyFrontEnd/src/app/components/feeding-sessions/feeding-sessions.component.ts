import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-feeding-sessions',
  templateUrl: './feeding-sessions.component.html',
  styleUrls: ['./feeding-sessions.component.css']
})
export class FeedingSessionsComponent implements OnInit {
  feedingSessions: any[] = [];
  formData: FormGroup;
  averageDuration: number = 0;
  averageMilk: number = 0;
  startDate: string | null = null;
  endDate: string | null = null;

  constructor(private http: HttpClient, private formBuilder: FormBuilder) {
    this.formData = this.formBuilder.group({
      milkConsumed: '',
      startTime: '',
      endTime: ''
    });
  }

  ngOnInit(): void {
    this.fetchFeedingSessions();
    this.calculateAverageDuration();
    this.calculateAverageMilk();
  }

  fetchFeedingSessions(): void {
    this.http
      .post('http://localhost:8080/api/feed/sessions', {
        userId: 1,
        startTime: null,
        endTime: null
      })
      .subscribe((response: any) => {
        this.feedingSessions = response;
      });
  }

  createFeedingSession(): void {
    this.http
      .post('http://localhost:8080/api/feed/new', {
        milkConsumed: this.formData.value.milkConsumed,
        startTime: this.formData.value.startTime,
        endTime: this.formData.value.endTime,
        userId: 1
      })
      .subscribe(() => {
        this.fetchFeedingSessions();
      });
  }

  updateFeedingSession(session: any): void {
    this.http
      .put('http://localhost:8080/api/feed/update', session)
      .subscribe(() => {
        this.fetchFeedingSessions();
      });
  }

  deleteFeedingSession(sessionId: number): void {
    const requestBody = { id: sessionId };
    this.http.request('delete', 'http://localhost:8080/api/feed/delete', { body: requestBody }).subscribe(() => {
      // Refresh the list of feeding sessions
      this.fetchFeedingSessions();
    });
  }


  onStartDateChange(date: string) {
    if (date) {
      const startTime = 'T00:00';
      const startDateWithTime = date + startTime;
      this.formData.controls['startTime'].setValue(startDateWithTime);
    } else {
      this.formData.controls['startTime'].setValue(null);
    }
  }

  onEndDateChange(date: string) {
    if (date) {
      const endTime = 'T23:59';
      const endDateWithTime = date + endTime;
      this.formData.controls['endTime'].setValue(endDateWithTime);
    } else {
      this.formData.controls['endTime'].setValue(null);
    }
  }

  calculateAverageDuration(): void {
    const requestBody = {
      userId: localStorage.getItem('userId'),
      startTime: this.formData.value.startTime,
      endTime: this.formData.value.endTime
    };

    this.http.post('http://localhost:8080/api/feed/averageDuration', requestBody).subscribe((response: any) => {
      this.averageDuration = response.averageDuration;
    });
  }

  calculateAverageMilk(): void {
    const requestBody = {
      userId: localStorage.getItem('userId'),
      startTime: this.formData.value.startTime,
      endTime: this.formData.value.endTime
    };

    this.http.post('http://localhost:8080/api/feed/averageMilk', requestBody).subscribe((response: any) => {
      this.averageMilk = response.averageMilk;
    });
  }

}
