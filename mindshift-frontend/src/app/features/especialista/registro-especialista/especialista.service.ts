import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EspecialistaService {
  private http = inject(HttpClient);
  
  // CORRECCIÓN: Cambia 'especialistas' por 'profesionales'
// Asegúrate de que apunte a 'profesionales' (tal como lo registraste en el router)
private apiUrl = 'http://127.0.0.1:8000/api/profesionales/';
  obtenerEspecialistas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  registrarEspecialista(datosMedico: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, datosMedico);
  }
}