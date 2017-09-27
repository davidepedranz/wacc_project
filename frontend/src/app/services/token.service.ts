import { Injectable } from '@angular/core';

@Injectable()
export class TokenService {

  // key used to store the token in the local storage
  private readonly TOKEN_KEY = 'token';

  readToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }
}
