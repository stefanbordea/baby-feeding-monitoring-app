import { Component, OnInit } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { catchError, of, tap } from "rxjs";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  login(): void {
    if (this.loginForm.invalid) {
      // Handle form validation errors
      return;
    }

    const loginData = this.loginForm.value;

    this.http
      .post('http://localhost:8080/api/user/login', loginData)
      .pipe(
        tap((response: any) => {
          // Handle successful login
          // For example, store the credentials in local storage
          const encodedCredentials = btoa(`${loginData.username}:${loginData.password}`);
          localStorage.setItem('credentials', encodedCredentials);

          // Store other relevant data if needed
          localStorage.setItem('userId', response.id);

          this.router.navigate(['']);
        }),
        catchError((error: any) => {
          // Handle login error
          console.error('Login error:', error);
          // Show error message to the user or perform any other error handling
          return of(null); // Return an observable to continue the stream
        })
      )
      .subscribe();
  }
}
