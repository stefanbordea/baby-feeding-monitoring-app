import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const userCredentials = localStorage.getItem("credentials");
    if (userCredentials != null) {
      request = request.clone({
        setHeaders: {
          Authorization: `Basic ${userCredentials}`
        }
      });
    }

    return next.handle(request);
  }
}
