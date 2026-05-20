import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EspecialistaService {
  // Inyectamos el cliente HTTP nativo de Angular
  private http = inject(HttpClient);
  
  // URL exacta de tu servidor de Django que está en verde
  private apiUrl = 'http://127.0.0.1:8000/api/especialistas/';

  // 📥 Traer todos los médicos de la BD (Por si los necesitas en un carrusel)
  obtenerEspecialistas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // 📤 Enviar los datos del formulario de 3 pasos directo a SQLite
  registrarEspecialista(datosMedico: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, datosMedico);
  }
}