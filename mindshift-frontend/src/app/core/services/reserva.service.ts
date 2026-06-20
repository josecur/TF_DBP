import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8000/api/reservas/';

  // ✅ Este es el método que Angular dice que no encuentra
// En reserva.service.ts
obtenerReservasPorUsuario(idUsuario: number): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}?idUsuario=${idUsuario}`).pipe(
    // Operador map para filtrar duplicados por ID antes de que lleguen al componente
    map((reservas: any[]) => {
      const unicas = new Map();
      reservas.forEach(r => unicas.set(r.id, r));
      return Array.from(unicas.values());
    })
  );
}

  crearReserva(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }

  obtenerTodasLasReservas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
  
}