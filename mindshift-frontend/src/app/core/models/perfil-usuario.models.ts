export interface PerfilUsuario {
  id:               number;
  nombres:          string;
  apellido:         string;   // ✅ singular — coincide con Django
  correo:           string;
  telefono?:        string;
  nivelCargaMental?: string;
  puntaje?:         number;
  historial?:       any[];
}