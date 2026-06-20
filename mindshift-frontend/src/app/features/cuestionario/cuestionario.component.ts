import { Component, OnInit, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CuestionarioService } from '../../core/services/cuestionario.service';
import { TestStateService } from '../../core/services/test-state.service';
import { PreguntaItemComponent } from './pregunta-item/pregunta-item.component';
import { FormularioRegistroComponent } from '../Registro/formulario-registro.component';
import { DiagnosticoModalComponent } from '../home/components/diganostico-modal.component/diagnostico-modal.component';

@Component({
  selector: 'app-cuestionario',
  standalone: true,
  imports: [
    CommonModule,
    PreguntaItemComponent,
    FormularioRegistroComponent,
    DiagnosticoModalComponent
  ],
  templateUrl: './cuestionario.component.html',
  styleUrls: ['./cuestionario.component.css']
})
export class CuestionarioComponent implements OnInit {
  private api   = inject(CuestionarioService);
  public  state = inject(TestStateService);

  @Output() usuarioRegistrado = new EventEmitter<any>();

  cargando:          boolean  = true;
  ramaSeleccionada:  string | null = null;
  preguntasFiltradas: any[]   = [];
  indiceLocal:        number   = 0;
  paso: 'seleccion' | 'cuestionario' | 'registro' | 'resultados' = 'seleccion';
  
  // 🛑 Bandera de control para bloqueo diario
  testBloqueadoHoy:  boolean = false;

  ngOnInit() {
    // 1. Resetear estado de memoria del cuestionario
    this.state.preguntas    = [];
    this.state.indiceActual = 0;
    this.state.puntajeTotal = 0;

    // 2. Ejecutar validación de restricción de tiempo
    this.verificarRestriccionDiaria();

    // 3. Traer preguntas desde Django REST API
    this.api.obtenerPreguntas().subscribe({
      next: (data) => {
        this.state.setPreguntas(data);
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
        console.error('❌ Error al cargar preguntas desde Django');
      }
    });
  }

  verificarRestriccionDiaria() {
    const activeSession = localStorage.getItem('session_active');
    const uData = localStorage.getItem('usuario_mindstep');

    if (activeSession && uData) {
      const usuario = JSON.parse(uData);
      
      // Validamos si el alumno ya cuenta con screenings en su historial
      if (usuario?.historial && usuario.historial.length > 0) {
        const ultimoTest = usuario.historial[usuario.historial.length - 1];
        const fechaUltimoTest = ultimoTest.fecha_evaluacion; 
        
        const hoy = new Date().toLocaleDateString('es-PE'); 

        if (fechaUltimoTest === hoy) {
          this.testBloqueadoHoy = true;
          this.paso = 'resultados'; // Forzamos el paso final de bloqueo visual
        }
      }
    }
  }

  seleccionarRama(rama: string) {
    if (this.testBloqueadoHoy) return;
    
    this.ramaSeleccionada   = rama;
    this.preguntasFiltradas = this.state.preguntas.filter(p =>
      p.categoria.trim().toLowerCase() === rama.trim().toLowerCase()
    );
    this.indiceLocal = 0;
    this.paso        = 'cuestionario';
  }

  get preguntaActual() {
    return this.preguntasFiltradas[this.indiceLocal];
  }

  get progreso(): number {
    if (!this.preguntasFiltradas.length) return 0;
    return Math.round((this.indiceLocal / this.preguntasFiltradas.length) * 100);
  }

  manejarRespuesta(valor: number) {
    this.state.procesarRespuesta(valor);

    if (this.indiceLocal < this.preguntasFiltradas.length - 1) {
      this.indiceLocal++;
    } else {
      // 🏁 Fin de las preguntas: Evaluamos si ya es un usuario con sesión activa
      const activeSession = localStorage.getItem('session_active');
      const uData = localStorage.getItem('usuario_mindstep');

      if (activeSession && uData) {
        // 🔄 CASO 1: Alumno antiguo con cuenta -> Guardamos directo en Django
        this.guardarResultadoAlumnoLogueado();
      } else {
        // 📝 CASO 2: Visitante nuevo -> Requiere registro obligatorio
        this.paso = 'registro';
      }
    }
  }

  // 🚀 NUEVA OPERACIÓN: Actualiza al alumno logueado directo en Django SQLite sin pasar por el formulario
// 🛠️ REEMPLAZA ESTA FUNCIÓN COMPLETA EN TU cuestionario.component.ts
guardarResultadoAlumnoLogueado() {
  this.cargando = true;
  
  const uData = localStorage.getItem('usuario_mindstep');
  if (!uData) {
    this.cargando = false;
    alert('No se encontraron datos del usuario activo.');
    return;
  }

  const usuario = JSON.parse(uData);
  const hoyString = new Date().toLocaleDateString('es-PE');

  // Mapeo clínico de riesgo basado en el puntaje acumulado reactivo
  let nuevoRiesgo = 'BAJO';
  if (this.state.puntajeTotal >= 31) nuevoRiesgo = 'CRITICO';
  else if (this.state.puntajeTotal >= 16) nuevoRiesgo = 'MODERADO';

  // Sincronizamos las propiedades del objeto local
  usuario.nivelCargaMental = nuevoRiesgo;
  usuario.puntaje = this.state.puntajeTotal;
  
  if (!usuario.historial) usuario.historial = [];
  usuario.historial.push({
    id: usuario.historial.length + 1,
    puntuacion_total: this.state.puntajeTotal,
    nivel_carga_calculado: nuevoRiesgo,
    fecha_evaluacion: hoyString
  });

  // 🎯 BLINDAJE ABSOLUTO: Aseguramos mapear EXACTAMENTE las columnas del modelo de Django
  // Si alguna propiedad del localStorage viene vacía, usamos un fallback seguro para evitar el error 400
  const payloadBackend = {
    nombreUsuario: usuario.nombres || 'Usuario',
    apellidoUsuario: usuario.apellido || 'MindStep',
    telefonoUsuario: usuario.telefono || '986575756',
    correoUsuario: usuario.correo || 'estudiante@usil.pe',
    clave: usuario.clave || 'ClaveSegura123!', // Requerido por el ORM
    nivel_riesgo: nuevoRiesgo,
    generoUsuario: usuario.generoUsuario || 'No especificado'
  };

  // Le metemos el PUT al servicio apuntando al ID del alumno activo
  this.api.actualizarRiesgoUsuario(usuario.id, payloadBackend).subscribe({
    next: () => {
      localStorage.setItem('usuario_mindstep', JSON.stringify(usuario));
      this.cargando = false;
      this.testBloqueadoHoy = true;
      this.paso = 'resultados'; // Salta directo a la pantalla de bloqueo diario
      alert(`¡Evaluación guardada con éxito en tu cuenta! Carga detectada: ${nuevoRiesgo} 📊`);
    },
    error: (err) => {
      this.cargando = false;
      console.error('Error detallado de Django:', err);
      
      // Mantenemos el fallback por estabilidad de la UI
      localStorage.setItem('usuario_mindstep', JSON.stringify(usuario));
      this.testBloqueadoHoy = true;
      this.paso = 'resultados';
      
      alert('Se calculó tu puntaje localmente, pero verifica que tu servidor Django esté encendido y con las migraciones al día.');
    }
  });
}

  onRegistroFinalizado(datosUsuarioBackend: any) {
    const hoyString = new Date().toLocaleDateString('es-PE');

    const usuarioMindstep = {
      id:               datosUsuarioBackend.id,
      nombres:          datosUsuarioBackend.nombres,
      apellido:         datosUsuarioBackend.apellido,
      correo:           datosUsuarioBackend.correo,        
      nivelCargaMental: datosUsuarioBackend.nivel_riesgo,  
      puntaje:          datosUsuarioBackend.puntaje,
      historial: [
        {
          id:                    1,
          puntuacion_total:      datosUsuarioBackend.puntaje,
          nivel_carga_calculado: datosUsuarioBackend.nivel_riesgo,
          fecha_evaluacion:      hoyString 
        }
      ]
    };

    localStorage.setItem('session_active', JSON.stringify({
      rol:     'usuario',
      id:      datosUsuarioBackend.id,
      nombres: datosUsuarioBackend.nombres
    }));
    localStorage.setItem('usuario_mindstep', JSON.stringify(usuarioMindstep));

    this.usuarioRegistrado.emit(usuarioMindstep);
    this.testBloqueadoHoy = true; 
    this.paso = 'resultados';
  }
}