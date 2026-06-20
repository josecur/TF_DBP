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
  private http   = inject(HttpClient);
  private apiUrl = 'http://localhost:8000/api';

  alCerrar        = output<void>();
  rolSeleccionado = signal<'usuario' | 'especialista'>('usuario');
  errorMensaje    = signal<string | null>(null);
  cargando        = signal<boolean>(false);

  credenciales = {
    identificador: '',
    password:      ''
  };

  cambiarRol(nuevoRol: 'usuario' | 'especialista') {
    this.rolSeleccionado.set(nuevoRol);
    this.errorMensaje.set(null);
    // Limpiamos los campos al cambiar de pestaña para evitar cruces
    this.credenciales.identificador = '';
    this.credenciales.password = '';
  }

  cerrarModal() {
    this.alCerrar.emit();
  }

  procesarLogin() {
    this.errorMensaje.set(null);
    const id   = this.credenciales.identificador.trim();
    const pass = this.credenciales.password.trim();

    if (!id || !pass) {
      this.errorMensaje.set('Completa todos los campos.');
      return;
    }

    this.cargando.set(true);

    if (this.rolSeleccionado() === 'usuario') {
      this.loginUsuario(id, pass);
    } else {
      this.loginEspecialista(id, pass);
    }
  }

  private loginUsuario(id: string, pass: string) {
    const payload = { username: id, password: pass };

    this.http.post<any>(`${this.apiUrl}/usuarios/login/`, payload).subscribe({
      next: (res) => {
        this.cargando.set(false);

        // ✅ Sesión limpia de Alumno
        localStorage.setItem('session_active', JSON.stringify({
          rol:     'usuario',
          id:      res.id,
          nombres: res.nombres
        }));

        if (res.nivel_riesgo) {
          localStorage.setItem('usuario_mindstep', JSON.stringify({
            id:               res.id,
            nombres:          res.nombres,
            apellido:         res.apellido  ?? '',
            correo:           res.correo    ?? id,
            nivelCargaMental: res.nivel_riesgo,
            puntaje:          0,
            historial:        []
          }));
        }

        this.cerrarModal();
        this.router.navigate(['/']).then(() => window.location.reload());
      },
      error: (err) => {
        this.cargando.set(false);
        console.error('Error login usuario:', err);
        this.errorMensaje.set('Correo o contraseña incorrectos.');
      }
    });
  }

private loginEspecialista(id: string, pass: string) {
  const payload = { username: id, password: pass };

  this.http.post<any>(`${this.apiUrl}/profesionales/login/`, payload).subscribe({
    next: (res) => {
      this.cargando.set(false);

      // ✅ Misma estructura completa para que el panel funcione
      const sesionCompleta = {
        rol:                     'especialista',
        id:                      res.id,
        nombres:                 res.nombreProfesional,
        nombreProfesional:       res.nombreProfesional,
        apellidoProfesional:     res.apellidoProfesional  || '',
        especialidad:            res.especialidad,
        avatar_icono:            res.avatar_icono            || '',
        descripcion_trayectoria: res.descripcion_trayectoria || '',
        enlace_agenda:           res.enlace_agenda            || '',
        generoProfesional:       res.generoProfesional        || 'Masculino',
        validacion:              res.validacion
      };

      localStorage.setItem('session_active',      JSON.stringify(sesionCompleta));
      localStorage.setItem('usuario_especialista', JSON.stringify(sesionCompleta));

      this.cerrarModal();
      this.router.navigate(['/dashboard-especialista'])
          .then(() => window.location.reload());
    },
    error: (err) => {
      this.cargando.set(false);
      this.errorMensaje.set('Credenciales de especialista incorrectas.');
    }
  });
}
}