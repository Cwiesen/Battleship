import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token-storage.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private tokenStor: TokenStorageService){}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let token: string = this.tokenStor.getToken();
        if(token) {
            req = req.clone({
                headers: req.headers.set("Authorization", "Bearer " + token)
            })
        }
        return next.handle(req);
    }
}