import { Component, computed, signal, inject, output } from '@angular/core'; // 👈 Importamos output
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface Pregunta {
  id: number;
  texto: string;
  dimension: string;
}

export interface IntentoCuestionario {
  fecha: string;
  nivelCargaMental: 'BAJO' | 'MODERADO' | 'CRITICO';
  respuestas: Record<number, number>;
}

export interface PerfilUsuario {
  id: string;
  nombres: string;
  apellidos: string;
  correo: string;
  telefono: string;
  ocupacion: string;
  sector: string;
  modalidad: string;
  nivelCargaMental: 'BAJO' | 'MODERADO' | 'CRITICO';
  historial: IntentoCuestionario[];
}

@Component({
  selector: 'app-cuestionario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cuestionario.component.html',
  styleUrls: ['./cuestionario.component.css']
})
export class CuestionarioComponent {
  
  private router = inject(Router);

  // 🚀 EVENTO DE SALIDA: Le avisa a la Home que hay un cambio en el usuario logueado
  actualizarHome = output<PerfilUsuario>();

  preguntas = signal<Pregunta[]>([
    { id: 1, texto: '¿Sueles anticipar el peor resultado posible ante una situación de incertidumbre diaria o laboral?', dimension: 'Catastrofismo' },
    { id: 2, texto: '¿Consideras que si no alcanzas el estándar máximo en una meta, todo tu esfuerzo es un fracaso total?', dimension: 'Pensamiento Polarizado' },
    { id: 3, texto: '¿Tiendes a asumir que tu entorno piensa negativamente de ti sin tener pruebas reales?', dimension: 'Lectura de Pensamiento' },
    { id: 4, texto: '¿Sientes que un pequeño contratiempo define por completo tu capacidad personal o profesional?', dimension: 'Sobregeneralización' }
  ]);

  indiceActual = signal<number>(0);
  respuestas = signal<Record<number, number>>({});
  
  estadoFlujo = signal<'QUIZ' | 'AUTH' | 'RESULTADOS'>('QUIZ');
  modoAuth = signal<'LOGIN' | 'REGISTER'>('LOGIN');
  verPassword = signal<boolean>(false);

  txtNombre = signal<string>('');
  txtApellido = signal<string>('');
  txtTelefono = signal<string>('');
  txtCorreo = signal<string>('');
  txtPassword = signal<string>('');

  preguntaActual = computed(() => this.preguntas()[this.indiceActual()]);
  progreso = computed(() => Math.round(((this.indiceActual() + 1) / this.preguntas().length) * 100));
  esUltimaPregunta = computed(() => this.indiceActual() === this.preguntas().length - 1);

  formValido = computed(() => {
    if (this.modoAuth() === 'LOGIN') {
      return this.txtCorreo().trim() !== '' && this.txtPassword().trim() !== '';
    } else {
      return this.txtNombre().trim() !== '' && 
             this.txtApellido().trim() !== '' && 
             this.txtTelefono().trim() !== '' && 
             this.txtCorreo().trim() !== '' && 
             this.txtPassword().trim() !== '';
    }
  });

  seleccionarOpcion(puntaje: number) {
    const idPregunta = this.preguntaActual().id;
    this.respuestas.update(prev => ({ ...prev, [idPregunta]: puntaje }));

    if (!this.esUltimaPregunta()) {
      this.indiceActual.update(idx => idx + 1);
    } else {
      const datosPrevios = localStorage.getItem('usuario_mindstep');
      if (datosPrevios) {
        this.ejecutarAutenticacion();
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
  }

  toggleVisibilidadPassword() {
    this.verPassword.update(v => !v);
  }

  ejecutarAutenticacion() {
    const datosPrevios = localStorage.getItem('usuario_mindstep');
    let usuarioActual: PerfilUsuario | null = datosPrevios ? JSON.parse(datosPrevios) : null;

    const niveles: ('BAJO' | 'MODERADO' | 'CRITICO')[] = ['BAJO', 'MODERADO', 'CRITICO'];
    const nivelCalculado = niveles[Math.floor(Math.random() * 3)];

    const nuevoIntento: IntentoCuestionario = {
      fecha: new Date().toLocaleDateString('es-PE', { 
        day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' 
      }),
      nivelCargaMental: nivelCalculado,
      respuestas: this.respuestas()
    };

    if (usuarioActual) {
      if (!usuarioActual.historial) usuarioActual.historial = [];
      usuarioActual.historial.push(nuevoIntento);
      usuarioActual.nivelCargaMental = nivelCalculado;
    } else {
      usuarioActual = {
        id: 'MND-' + Math.floor(Math.random() * 900000 + 100000),
        nombres: this.txtNombre() || 'Usuario',
        apellidos: this.txtApellido() || 'MindStep',
        correo: this.txtCorreo(),
        telefono: this.txtTelefono(),
        ocupacion: 'PROFESIONAL / INDEPENDIENTE', 
        sector: 'SERVICIOS Y PRODUCTIVIDAD',
        modalidad: 'HÍBRIDO',
        nivelCargaMental: nivelCalculado,
        historial: [nuevoIntento]
      };
    }

    localStorage.setItem('usuario_mindstep', JSON.stringify(usuarioActual));

    // 🚀 AQUÍ ESTÁ EL TRUCO: Emitimos el usuario a la Home de manera reactiva limpia
    this.actualizarHome.emit(usuarioActual);

    // Cambiamos el estado local para pintar el resultado de una vez
    this.estadoFlujo.set('RESULTADOS'); 
    
    // Reseteamos el índice por si quieren repetir el test luego
    this.indiceActual.set(0);
  }
}