import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SessionCreateRequest } from '../models/http/requests';
import { SessionCreateResponse } from '../models/http/responses';
import { ApiRoutes } from '../utils/api-routes';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private httpClient: HttpClient) { }

  public createSession(userName: string, sessionName: string, password: string | null, publicSession: boolean): Promise<SessionCreateResponse> {
    
    const payload: SessionCreateRequest = {
      userName,
      sessionName,
      password,
      publicSession
    };

    return this.httpClient.post(ApiRoutes.Session.Create, payload).toPromise();
  }
}
