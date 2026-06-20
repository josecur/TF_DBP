export interface PerfilUsuario {
  id: number;
  nombres: string;
  apellidos: string;
  correo: string;
  telefono?: string;
  carrera?: string;
  nivelCargaMental?: string;
  historial?: any[];
}