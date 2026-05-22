import { Component, computed, signal, inject, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface Pregunta {
  id: number;
  texto: string;
  dimension: string;
}

export interface IntentoCuestionario {
  id?: number;
  alumno_usuario?: number;
  usuario_nombre: string;
  puntuacion_total: number;
  distorsion_predominante: string;
  nivel_carga_calculado: 'BAJO' | 'MODERADO' | 'CRITICO';
  fecha_evaluacion?: string;
}

export interface PerfilUsuario {
  id: string | number;
  nombres: string;
  apellidos: string;
  correo: string;
  telefono?: string;
  codigo_identificacion?: string;
  carrera?: string;
  nivelCargaMental: 'BAJO' | 'MODERADO' | 'CRITICO';
  historial?: IntentoCuestionario[];
}

@Component({
  selector: 'app-cuestionario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  templateUrl: './cuestionario.component.html',
  styleUrls: ['./cuestionario.component.css']
})
export class CuestionarioComponent implements OnInit {
  private router = inject(Router);
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8000/api/alumnos/';
  private apiTestUrl = 'http://localhost:8000/api/historial-tests/';

  // 🎯 COMUNICACIÓN DE EVENTOS FORMAL CON EL HOME
  @Output() actualizarHome = new EventEmitter<PerfilUsuario>();

  usuarioLogueado = signal<any | null>(null);
  modoReevaluacion = signal<boolean>(false);
  mostrarModalLogin = signal<boolean>(false);
  mostrarModal = signal<boolean>(false);

  preguntas = signal<Pregunta[]>([
    { id: 1, texto: '¿Sueles anticipar el peor resultado posible ante una situación de incertidumbre diaria o en tus estudios?', dimension: 'Catastrofismo' },
    { id: 2, texto: '¿Consideras que si no alcanzas el estándar máximo en una meta, todo tu esfuerzo es un fracaso total?', dimension: 'Pensamiento Polarizado' },
    { id: 3, texto: '¿Tiendes a asumir que tu entorno piensa negativamente de ti sin tener pruebas reales?', dimension: 'Lectura de Pensamiento' },
    { id: 4, texto: '¿Sientes que un pequeño contratiempo define por completo tu capacidad personal o académica?', dimension: 'Sobregeneralización' }
  ]);

  indiceActual = signal<number>(0);
  respuestas = signal<Record<number, number>>({});
  
  estadoFlujo = signal<'QUIZ' | 'AUTH' | 'RESULTADOS'>('QUIZ');
  modoAuth = signal<'LOGIN' | 'REGISTER'>('LOGIN');
  verPassword = signal<boolean>(false);
  errorMensaje = signal<string | null>(null);

  txtNombre = signal<string>('');
  txtApellido = signal<string>('');
  txtTelefono = signal<string>('');
  txtCorreo = signal<string>('');
  txtPassword = signal<string>('');
  txtCodigo = signal<string>(''); 

  preguntaActual = computed(() => this.preguntas()[this.indiceActual()]);
  progreso = computed(() => Math.round(((this.indiceActual() + 1) / this.preguntas().length) * 100));
  esUltimaPregunta = computed(() => this.indiceActual() === this.preguntas().length - 1);

  formValido = computed(() => {
    if (this.modoAuth() === 'LOGIN') {
      return this.txtCorreo().trim() !== '' && this.txtPassword().trim() !== '';
    } else {
      return this.txtNombre().trim() !== '' && 
             this.txtApellido().trim() !== '' && 
             this.txtCorreo().trim() !== '' && 
             this.txtPassword().trim() !== '' &&
             this.txtCodigo().trim() !== '';
    }
  });

  ngOnInit() {
    const active = localStorage.getItem('session_active');
    if (active) this.usuarioLogueado.set(JSON.parse(active));
  }

  seleccionarOpcion(puntaje: number) {
    const idPregunta = this.preguntaActual().id;
    this.respuestas.update(prev => ({ ...prev, [idPregunta]: puntaje }));

    if (!this.esUltimaPregunta()) {
      this.indiceActual.update(idx => idx + 1);
    } else {
      const activeSession = localStorage.getItem('session_active');
      if (activeSession) {
        this.guardarTestDirecto(JSON.parse(activeSession).id, JSON.parse(activeSession).nombres);
      } else {
        this.estadoFlujo.set('AUTH'); 
      }
    }
  }

  regresar() {
    if (this.indiceActual() > 0) {
      this.indiceActual.update(idx => idx - 1);
    }
  }

  cambiarModoAuth(modo: 'LOGIN' | 'REGISTER') {
    this.modoAuth.set(modo);
    this.txtNombre.set('');
    this.txtApellido.set('');
    this.txtTelefono.set('');
    this.txtCorreo.set('');
    this.txtPassword.set('');
    this.txtCodigo.set('');
    this.errorMensaje.set(null);
  }

  toggleVisibilidadPassword() {
    this.verPassword.update(v => !v);
  }

  abrirLogin() { this.mostrarModalLogin.set(true); }
  cerrarLogin() { this.mostrarModalLogin.set(false); }
  activarReevaluacion() { this.modoReevaluacion.set(true); }
  cancelarReevaluacion() { this.modoReevaluacion.set(false); }

  ejecutarAutenticacion() {
    this.errorMensaje.set(null);
    const modo = this.modoAuth();

    let sumaPuntajes = 0;
    Object.values(this.respuestas()).forEach(val => sumaPuntajes += val);
    
    let nivelCalculado: 'BAJO' | 'MODERADO' | 'CRITICO' = 'BAJO';
    if (sumaPuntajes >= 4 && sumaPuntajes <= 6) nivelCalculado = 'MODERADO';
    if (sumaPuntajes > 6) nivelCalculado = 'CRITICO';

    const distorsionPredominante = this.preguntas()[Math.floor(Math.random() * this.preguntas().length)].dimension;

    if (modo === 'LOGIN') {
      const payloadLogin = { correo: this.txtCorreo().trim(), password: this.txtPassword().trim() };
      
      this.http.post<any>(`${this.apiUrl}login/`, payloadLogin).subscribe({
        next: (alumno) => {
          this.registrarIntentoTest(alumno.id, `${alumno.nombres} ${alumno.apellidos}`, sumaPuntajes, distorsionPredominante, nivelCalculado);
        },
        error: () => this.errorMensaje.set('Credenciales inválidas. Verifica tu correo o contraseña.')
      });

    } else {
      const payloadRegister = {
        username: this.txtCorreo().trim().split('@')[0],
        password_hash: this.txtPassword().trim(),
        correo: this.txtCorreo().trim(),
        nombres: this.txtNombre().trim(),
        apellidos: this.txtApellido().trim(),
        codigo_identificacion: this.txtCodigo().trim(),
        carrera: 'Escolar',
        telefono: this.txtTelefono().trim(),
        ultimo_nivel_carga: nivelCalculado
      };

      this.http.post<any>(this.apiUrl, payloadRegister).subscribe({
        next: (nuevoAlumno) => {
          this.registrarIntentoTest(nuevoAlumno.id, `${nuevoAlumno.nombres} ${nuevoAlumno.apellidos}`, sumaPuntajes, distorsionPredominante, nivelCalculado);
        },
        error: () => this.errorMensaje.set('Error al registrar cuenta. El código o correo ya existen.')
      });
    }
  }

  private registrarIntentoTest(alumnoId: number, nombreCompleto: string, puntos: number, distorsion: string, nivel: 'BAJO' | 'MODERADO' | 'CRITICO') {
    const payloadTest = {
      alumno_usuario: alumnoId,
      usuario_nombre: nombreCompleto,
      puntuacion_total: puntos,
      distorsion_predominante: distorsion,
      nivel_carga_calculado: nivel
    };

    this.http.post<any>(this.apiTestUrl, payloadTest).subscribe({
      next: () => {
        const perfilCompleto: PerfilUsuario = {
          id: alumnoId,
          nombres: nombreCompleto.split(' ')[0],
          apellidos: nombreCompleto.split(' ').slice(1).join(' '),
          correo: this.txtCorreo().trim(),
          telefono: this.txtTelefono().trim(),
          nivelCargaMental: nivel,
          historial: [payloadTest]
        };

        // Guardamos los estados relacionales en local
        localStorage.setItem('session_active', JSON.stringify({ rol: 'usuario', id: alumnoId, nombres: nombreCompleto }));
        localStorage.setItem('usuario_mindstep', JSON.stringify(perfilCompleto));
        
        // 🎯 DISPARADOR EN CADENA: Avisa al Home que ya hay usuario y datos listos
        this.actualizarHome.emit(perfilCompleto);

        // Cambiamos el flujo local de manera limpia sin recargar
        this.estadoFlujo.set('RESULTADOS'); 
        this.indiceActual.set(0);
      },
      error: () => this.errorMensaje.set('Error al registrar tus respuestas en el screening.')
    });
  }

  private guardarTestDirecto(alumnoId: number, nombreCompleto: string) {
    let sumaPuntajes = 0;
    Object.values(this.respuestas()).forEach(val => sumaPuntajes += val);
    let nivelCalculado: 'BAJO' | 'MODERADO' | 'CRITICO' = 'BAJO';
    if (sumaPuntajes >= 4 && sumaPuntajes <= 6) nivelCalculado = 'MODERADO';
    if (sumaPuntajes > 6) nivelCalculado = 'CRITICO';
    this.registrarIntentoTest(alumnoId, nombreCompleto, sumaPuntajes, 'Pensamiento Autocrítico', nivelCalculado);
  }

  cerrarSesionDesdeNavbar() {
    localStorage.clear();
    this.router.navigate(['/']).then(() => window.location.reload());
  }
}