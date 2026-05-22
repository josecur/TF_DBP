import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http'; 
import { EspecialistaService } from '../registro-especialista/especialista.service'; 

@Component({
  selector: 'app-perfil-especialista',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule],
  templateUrl: './perfil-especialista.component.html',
  styleUrls: ['./perfil-especialista.component.css']
})
export class PerfilEspecialistaComponent implements OnInit {
  private route = inject(ActivatedRoute); 
  private especialistaService = inject(EspecialistaService); 
  private http = inject(HttpClient); 

  especialista = signal<any | null>(null);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.cargarDetalleEspecialista(Number(idParam));
      }
    });
  }

  cargarDetalleEspecialista(id: number) {
    this.especialistaService.obtenerEspecialistas().subscribe({
      next: (lista: any[]) => {
        const encontrado = lista.find(medico => medico.id == id);
        if (encontrado) {
          // Aseguramos que el objeto siempre contenga el arreglo de publicaciones para evitar fallos en el @for
          if (!encontrado.publicaciones) {
            encontrado.publicaciones = [];
          }
          this.especialista.set(encontrado);
        }
      },
      error: (err) => console.error('Error al mapear doctor desde SQLite:', err)
    });
  }

  solicitarConsulta() {
    const datosAlumno = localStorage.getItem('usuario_mindstep');
    if (!datosAlumno) {
      alert('Debes iniciar sesión como estudiante para solicitar una consulta.');
      return;
    }
    const alumno = JSON.parse(datosAlumno);

    const payload = {
      especialista: this.especialista()?.id, 
      alumno_nombre: `${alumno.nombres} ${alumno.apellidos}`,
      alumno_correo: alumno.correo,
      motivo: `Derivación automática. El alumno presenta un nivel de carga mental estimado como ${alumno.nivelCargaMental || 'MODERADO'}.`,
      estado: 'PENDIENTE'
    };

    this.http.post('http://localhost:8000/api/consultas/', payload).subscribe({
      next: () => {
        alert('¡Tu solicitud de consulta express fue enviada al especialista! Aparecerá en su panel al instante.');
      },
      error: (err) => {
        console.error('Error al mandar cita:', err);
        alert('No se pudo procesar la consulta en este momento.');
      }
    });
  }
}