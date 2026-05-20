import { Component, signal, output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private router = inject(Router);

  // 🚪 Evento de salida para avisarle al Navbar cuándo cerrar el Popup
  alCerrar = output<void>();

  // Rol activo por defecto: 'usuario' o 'especialista'
  rolSeleccionado = signal<'usuario' | 'especialista'>('usuario');

  // Campos del formulario
  credenciales = {
    identificador: '', // Puede ser correo o usuario
    password: ''
  };

  cambiarRol(nuevoRol: 'usuario' | 'especialista') {
    this.rolSeleccionado.set(nuevoRol);
    this.credenciales.identificador = '';
    this.credenciales.password = '';
  }

  cerrarModal() {
    this.alCerrar.emit();
  }

  procesarLogin() {
    const rol = this.rolSeleccionado();
    const id = this.credenciales.identificador.trim();
    const pass = this.credenciales.password.trim();

    if (!id || !pass) return;

    if (rol === 'usuario') {
      // 📝 Validación para Usuario/Estudiante local
      const usuarioLocal = localStorage.getItem('usuario_logueado');
      if (usuarioLocal) {
        const u = JSON.parse(usuarioLocal);
        // Simulamos login exitoso con coincidencia básica para el MVP
        localStorage.setItem('session_active', JSON.stringify({ rol: 'usuario', nombres: u.nombres || id }));
        alert(`¡Bienvenido de vuelta, ${u.nombres || id}!`);
        this.cerrarModal();
        this.router.navigate(['/user-profile']);
      } else {
        // Cuenta genérica por si no ha hecho el test
        localStorage.setItem('session_active', JSON.stringify({ rol: 'usuario', nombres: id }));
        alert(`Sesión iniciada como ${id}`);
        this.cerrarModal();
        this.router.navigate(['/user-profile']);
      }
    } else {
      // 🩺 Validación para Especialista/Médico
      const medicosGuardados = localStorage.getItem('staff_especialistas');
      const lista = medicosGuardados ? JSON.parse(medicosGuardados) : [];
      
      // Busca si el usuario/correo ingresado coincide con alguno registrado en la Red
      const medicoEncontrado = lista.find((m: any) => m.nombre.toLowerCase().includes(id.toLowerCase()));

      localStorage.setItem('session_active', JSON.stringify({ rol: 'especialista', nombres: medicoEncontrado ? medicoEncontrado.nombre : id }));
      alert(`Portal Médico: Acceso concedido al ${medicoEncontrado ? medicoEncontrado.nombre : id}`);
      this.cerrarModal();
      this.router.navigate(['/']); // Regresa a la Home a ver el feed actualizado
    }
  }
}