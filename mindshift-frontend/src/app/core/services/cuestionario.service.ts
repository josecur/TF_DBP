import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CuestionarioService {
  actualizarRiesgoUsuario(id: any, payloadBackend: { nombreUsuario: any; apellidoUsuario: any; telefonoUsuario: any; correoUsuario: any; nivel_riesgo: string; }) {
return this.http.put<any>(`http://localhost:8000/api/usuarios/${id}/`, payloadBackend);  }
  private apiUrl = 'http://127.0.0.1:8000/api';

  constructor(private http: HttpClient) {}

  obtenerPreguntas(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/escenarios/`);
  }

  enviarResultado(resultado: { puntaje: number }): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuarios/guardar-resultado/`, resultado);
  }
}