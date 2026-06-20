import { Component, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestStateService } from '../../core/services/test-state.service';

@Component({
  selector: 'app-formulario-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './formulario-registro.component.html',
  styleUrls: ['./formulario-registro.component.css']
})
export class FormularioRegistroComponent {
  private http      = inject(HttpClient);
  private testState = inject(TestStateService);

  esRegistro:   boolean = true;
  verPassword:  boolean = false;
  verConfirmar: boolean = false;
  cargando:     boolean = false;
  errorMsg:     string  = '';

  usuario = {
    nombre:            '',
    apellido:          '',
    email:             '',  // ✅ único campo, sirve para registro y login
    telefono:          '',
    password:          '',
    confirmarPassword: ''
  };

  @Output() registroExitoso = new EventEmitter<any>();

  enviarFormulario() {
    this.errorMsg = '';

    if (this.esRegistro) {
      this.registrar();
    } else {
      this.login();
    }
  }

  private registrar() {
    if (this.usuario.password !== this.usuario.confirmarPassword) {
      this.errorMsg = 'Las contraseñas no coinciden.';
      return;
    }

    if (!this.usuario.nombre || !this.usuario.apellido || !this.usuario.email || !this.usuario.telefono) {
      this.errorMsg = 'Por favor completa todos los campos.';
      return;
    }

    this.cargando = true;
    const url = 'http://localhost:8000/api/usuarios/registro/';

    const payload = {
      nombre:   this.usuario.nombre,
      apellido: this.usuario.apellido,
      email:    this.usuario.email,
      telefono: this.usuario.telefono,
      password: this.usuario.password,
      puntaje:  this.testState.puntajeTotal  // ✅ el dato que faltaba
    };

    this.http.post<any>(url, payload).subscribe({
      next: (res) => {
        this.cargando = false;
        console.log('✅ Usuario guardado en SQLite:', res);
        this.registroExitoso.emit(res);
      },
      error: (err) => {
        this.cargando = false;
        console.error('❌ Error al registrar:', err);
        this.errorMsg = 'Error de registro. Verifica que el correo no esté en uso.';
      }
    });
  }

  private login() {
    if (!this.usuario.email || !this.usuario.password) {
      this.errorMsg = 'Ingresa tu correo y contraseña.';
      return;
    }

    this.cargando = true;
    const url = 'http://localhost:8000/api/usuarios/login/';

    const payload = {
      username: this.usuario.email,    // ✅ views.py espera 'username' para el login
      password: this.usuario.password
    };

    this.http.post<any>(url, payload).subscribe({
      next: (res) => {
        this.cargando = false;
        console.log('✅ Login exitoso:', res);
        this.registroExitoso.emit(res);
      },
      error: (err) => {
        this.cargando = false;
        console.error('❌ Error de login:', err);
        this.errorMsg = 'Correo o contraseña incorrectos.';
      }
    });
  }
}