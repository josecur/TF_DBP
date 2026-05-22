import { Component, signal, output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private router = inject(Router);
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8000/api';

  // 🚪 Evento de salida para avisarle al Navbar cuándo cerrar el Popup
  alCerrar = output<void>();

  // Rol activo por defecto: 'usuario' o 'especialista'
  rolSeleccionado = signal<'usuario' | 'especialista'>('usuario');
  
  // Captura de errores para la UI
  errorMensaje = signal<string | null>(null);

  // Campos del formulario
  credenciales = {
    identificador: '', // Correo electrónico corporativo o personal
    password: ''
  };

  cambiarRol(nuevoRol: 'usuario' | 'especialista') {
    this.rolSeleccionado.set(nuevoRol);
    this.credenciales.identificador = '';
    this.credenciales.password = '';
    this.errorMensaje.set(null);
  }

  cerrarModal() {
    this.alCerrar.emit();
  }

  procesarLogin() {
    this.errorMensaje.set(null);
    const rol = this.rolSeleccionado();
    const id = this.credenciales.identificador.trim();
    const pass = this.credenciales.password.trim();

    if (!id || !pass) return;

    if (rol === 'usuario') {
      // 📥 Flujo Alumno/Estudiante Conectado a la API Real de Django
      const payload = { correo: id, password: pass };
      
      this.http.post<any>(`${this.apiUrl}/alumnos/login/`, payload).subscribe({
        next: (alumnoAutenticado) => {
          // 1. Guardamos los datos completos estructurados para el perfil
          localStorage.setItem('usuario_mindstep', JSON.stringify(alumnoAutenticado));
          
          // 2. Seteamos la sesión activa general
          localStorage.setItem('session_active', JSON.stringify({ 
            rol: 'usuario', 
            id: alumnoAutenticado.id,
            nombres: alumnoAutenticado.nombres,
            correo: alumnoAutenticado.correo
          }));

          this.cerrarModal();
          
          // 🚀 Navegación al perfil del alumno con recarga limpia
          this.router.navigate(['/user-profile']).then(() => window.location.reload());
        },
        error: (err) => {
          console.error('Error en autenticación de alumno:', err);
          this.errorMensaje.set('Las credenciales de estudiante no son válidas o no existen.');
        }
      });

    } else {
      // 🩺 Flujo Especialista/Médico Optimizado con la Base de Datos
      this.http.get<any[]>(`${this.apiUrl}/especialistas/`).subscribe({
        next: (listaMedicos) => {
          const medicoEncontrado = listaMedicos.find(m => 
            m.username.toLowerCase().trim() === id.toLowerCase().trim() || 
            m.correo.toLowerCase().trim() === id.toLowerCase().trim()
          );

          // Nota: Como los médicos se cargan desde el Admin de Django en tu backend,
          // validamos la coincidencia del password en texto claro para tu demostración del MVP
          if (medicoEncontrado && medicoEncontrado.password_hash === pass) {
            localStorage.setItem('usuario_especialista', JSON.stringify(medicoEncontrado));
            
            localStorage.setItem('session_active', JSON.stringify({ 
              rol: 'especialista', 
              nombres: `${medicoEncontrado.nombres} ${medicoEncontrado.apellidos}` 
            }));

            this.cerrarModal();
            this.router.navigate(['/dashboard-especialista']);
          } else {
            this.errorMensaje.set('Identificador médico o contraseña de red incorrectos.');
          }
        },
        error: (err) => {
          console.error('Error al consultar médicos de SQLite:', err);
          this.errorMensaje.set('Hubo un problema de conexión con el servidor de salud.');
        }
      });
    }
  }
}