import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { EspecialistaService } from '../registro-especialista/especialista.service'; 

interface Especialista {
  id?: number;
  nombres: string;
  apellidos: string;
  especialidad: string;
  descripcion_trayectoria: string;
  universidad: string;
  numero_colegiatura: string;
  pais: string;
  idiomas: string;
  publico_objetivo: string;
  enlace_agenda?: string;
  avatar_icono: string;
  fecha_registro?: string;
}

@Component({
  selector: 'app-perfil-especialista',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './perfil-especialista.component.html',
  styleUrls: ['./perfil-especialista.component.css']
})
export class PerfilEspecialistaComponent implements OnInit {
  private route = inject(ActivatedRoute); 
  private especialistaService = inject(EspecialistaService); 

  especialista = signal<Especialista | null>(null);

  ngOnInit() {
    // 🚀 Escuchamos el parámetro de forma asíncrona para asegurar la lectura del ID
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.cargarDetalleEspecialista(Number(idParam));
      }
    });
  }

  cargarDetalleEspecialista(id: number) {
    this.especialistaService.obtenerEspecialistas().subscribe({
      next: (lista: any[]) => {
        // 🎯 CORRECCIÓN CLAVE: Usamos '==' para evitar conflictos string vs number del JSON
        const encontrado = lista.find(medico => medico.id == id);
        
        if (encontrado) {
          this.especialista.set(encontrado);
        } else {
          console.warn(`No se encontró ningún especialista en SQLite con el ID: ${id}`);
        }
      },
      error: (err) => console.error('Error al recuperar detalle desde Django:', err)
    });
  }
}