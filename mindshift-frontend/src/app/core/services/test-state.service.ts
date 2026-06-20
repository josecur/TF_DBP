import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class TestStateService {
  preguntas: any[] = [];
  indiceActual = 0;
  puntajeTotal = 0;

  setPreguntas(data: any[]) { 
    this.preguntas = data; 
  }

  obtenerPreguntaActual() { 
    return this.preguntas[this.indiceActual]; 
  }

  procesarRespuesta(valor: number) {
    this.puntajeTotal += valor;
    this.indiceActual++;
  }

  estaTerminado(): boolean { 
    return this.preguntas.length > 0 && this.indiceActual >= this.preguntas.length; 
  }
}