import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; 
import { CuestionarioComponent, PerfilUsuario } from '../cuestionario/cuestionario.component';
import { AppSugerenciaComponent } from './components/sugerencia/sugerencia.component';
import { AppPanelComponent } from './components/panel/panel.component';
import { DiagnosticoModalComponent } from './components/diganostico-modal.component/diagnostico-modal.component';
import { LoginComponent } from '../login/login.component'; // 🚀 IMPORTACIÓN CORREGIDA (Ajusta la ruta según tus carpetas)

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule, 
    CuestionarioComponent, 
    AppSugerenciaComponent, 
    AppPanelComponent,
    DiagnosticoModalComponent,
    LoginComponent // 👈 Registrado para que funcione el popup flotante de login
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  estadoFlujo = signal<'QUIZ' | 'AUTH' | 'RESULTADOS'>('QUIZ');
  modoReevaluacion = signal<boolean>(false);

  // 🚀 SEÑALES DE CONTROL PARA EL MODAL DE DIAGNÓSTICO (TEST)
  mostrarModal = signal<boolean>(false);
  perfilReciente = signal<PerfilUsuario | null>(null);

  // 🚪 SEÑAL DE CONTROL PARA EL MODAL DE LOGIN (NAVBAR)
  mostrarModalLogin = signal<boolean>(false);

  ngOnInit() {
    // Sincroniza estado inicial si ya existe sesión
    this.verificarSesion();
  }

  // ⚙️ CÓMPUTO REACTIVO: Revisa si hay sesión activa para pintar el "👋 Hola" en el Navbar
  usuarioLogueado = computed(() => {
    const active = localStorage.getItem('session_active');
    return active ? JSON.parse(active) : null;
  });

  verificarSesion() {
    const active = localStorage.getItem('session_active');
    const datosTest = localStorage.getItem('usuario_mindstep');
    
    // Si hizo el test pero no tiene sesión activa en este navegador, los enlazamos
    if (datosTest && !active) {
      const u = JSON.parse(datosTest);
      localStorage.setItem('session_active', JSON.stringify({ rol: 'usuario', nombres: u.nombres }));
    }
  }

  // 🚀 ESCUCHADOR REACTIVO: Atrapa el término del cuestionario sin recargar la página
  onUsuarioActualizado(perfil: PerfilUsuario) {
    // Registra tanto la base del test como la sesión activa
    localStorage.setItem('usuario_mindstep', JSON.stringify(perfil));
    localStorage.setItem('session_active', JSON.stringify({ rol: 'usuario', nombres: perfil.nombres }));
    
    this.perfilReciente.set(perfil);       // Le pasa los datos frescos al modal
    this.modoReevaluacion.set(false);      // Esconde el cuestionario de la pantalla
    this.mostrarModal.set(true);           // ¡Bum! Levanta el popup flotante de diagnóstico
  }

  // MÉTODOS DE APERTURA Y CIERRE (LOGIN NAVBAR)
  abrirLogin() {
    this.mostrarModalLogin.set(true);
  }

  cerrarLogin() {
    this.mostrarModalLogin.set(false);
  }

  activarReevaluacion() {
    this.modoReevaluacion.set(true);
  }

  cancelarReevaluacion() {
    this.modoReevaluacion.set(false);
  }

  cerrarPopupDiagnostico() {
    this.mostrarModal.set(false);
  }
}