import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-panel-especialista',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  templateUrl: './panel-especialista.component.html',
  styleUrls: ['./panel-especialista.component.css']
})
export class PanelEspecialistaComponent implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = 'http://localhost:8000/api';

  medico = signal<any | null>(null);
  consultas = signal<any[]>([]);

  // Campos amarrados a los inputs mediante NgModel
  formNombres = '';
  formApellidos = '';
  formTrayectoria = '';
  formAgenda = '';
  formAvatar = ''; // Almacenará la cadena Base64 de la foto

  // Gestión de contraseñas
  formContra = '';
  formContraConfirmar = '';

  // Campos para redactar artículos
  nuevoTitulo = '';
  nuevoContenido = '';

  ngOnInit() {
    const sesion = localStorage.getItem('usuario_especialista');
    if (!sesion) {
      this.router.navigate(['/']);
      return;
    }
    
    const medicoData = JSON.parse(sesion);
    this.medico.set(medicoData);
    this.inicializarFormulario(medicoData);
    this.cargarConsultasAlumnos(medicoData.id);
  }

  inicializarFormulario(m: any) {
    this.formNombres = m.nombres || '';
    this.formApellidos = m.apellidos || '';
    this.formTrayectoria = m.descripcion_trayectoria || '';
    this.formAgenda = m.enlace_agenda || '';
    this.formAvatar = m.avatar_icono || ''; // Inicializa la foto actual desde SQLite
  }

  // 📥 LECTOR DE BITS: Convierte la foto local subida a Base64 para guardarla en el TextField de Django
  alCambiarFotoPerfil(event: any) {
    const archivo: File = event.target.files[0];
    if (archivo) {
      if (!archivo.type.startsWith('image/')) {
        alert('Selecciona un archivo de imagen válido.');
        return;
      }
      const lector = new FileReader();
      lector.onload = () => {
        this.formAvatar = lector.result as string; // Actualiza la miniatura en caliente en la UI
      };
      lector.readAsDataURL(archivo);
    }
  }

  // 🎯 MAPEADO RELACIONAL: Jala y limpia las citas que los usuarios generen en el Front
  cargarConsultasAlumnos(medicoId: number) {
    this.http.get<any[]>(`${this.apiUrl}/consultas/`).subscribe({
      next: (res) => {
        // Filtrar las solicitudes express dirigidas a este especialista
        const filtradas = res.filter(c => c.especialista === medicoId);
        
        // Formateo preventivo para que el template HTML lea el string de nombres sin problemas
        const mapeadas = filtradas.map(c => ({
          ...c,
          alumno_nombre: c.alumno_nombre || (c.alumno_usuario_detalle?.nombres + ' ' + c.alumno_usuario_detalle?.apellidos),
          alumno_correo: c.alumno_correo || c.alumno_usuario_detalle?.correo
        }));

        this.consultas.set(mapeadas);
      },
      error: (err) => console.error('Error al sincronizar alumnos desde SQLite:', err)
    });
  }

  guardarPerfil() {
    const id = this.medico()?.id;
    const payload = {
      ...this.medico(),
      nombres: this.formNombres,
      apellidos: this.formApellidos,
      descripcion_trayectoria: this.formTrayectoria,
      enlace_agenda: this.formAgenda,
      avatar_icono: this.formAvatar // Se envía la cadena binaria completa a la BD
    };

    this.http.put(`${this.apiUrl}/especialistas/${id}/`, payload).subscribe({
      next: (res: any) => {
        this.medico.set(res);
        localStorage.setItem('usuario_especialista', JSON.stringify(res));
        
        // Actualizar la navbar global de inmediato
        localStorage.setItem('session_active', JSON.stringify({ 
          rol: 'especialista', 
          nombres: `${res.nombres} ${res.apellidos}` 
        }));

        alert('¡Perfil y foto actualizados en SQLite con éxito!');
      },
      error: (err) => alert('Error al actualizar los datos en el backend.')
    });
  }

  actualizarContrasena() {
    if (!this.formContra || this.formContra !== this.formContraConfirmar) return;
    const id = this.medico()?.id;
    
    this.http.post(`${this.apiUrl}/especialistas/${id}/cambiar-contra/`, { nueva_contrasena: this.formContra }).subscribe({
      next: () => {
        this.formContra = '';
        this.formContraConfirmar = '';
        alert('¡Tu contraseña ha sido cambiada con éxito!');
      },
      error: (err) => alert('Error al procesar el cambio de contraseña.')
    });
  }

  publicarArticulo() {
    if (!this.nuevoTitulo || !this.nuevoContenido) return;
    
    const payload = {
      especialista_id: this.medico()?.id,
      titulo: this.nuevoTitulo,
      contenido: this.nuevoContenido,
      categoria: 'Carga Mental Universitaria'
    };

    this.http.post(`${this.apiUrl}/publicaciones/`, payload).subscribe({
      next: () => {
        this.nuevoTitulo = '';
        this.nuevoContenido = '';
        alert('¡Artículo lanzado con éxito al muro público!');
      },
      error: (err) => alert('Error al registrar la publicación.')
    });
  }

  atenderConsulta(consultaId: number) {
    this.http.patch(`${this.apiUrl}/consultas/${consultaId}/`, { estado: 'ATENDIDO' }).subscribe({
      next: () => {
        // Refrescar la lista de derivados al instante
        this.cargarConsultasAlumnos(this.medico()?.id);
      },
      error: (err) => console.error(err)
    });
  }

  cerrarSesion() {
    localStorage.removeItem('usuario_especialista');
    localStorage.removeItem('session_active');
    this.router.navigate(['/']).then(() => window.location.reload());
  }
}