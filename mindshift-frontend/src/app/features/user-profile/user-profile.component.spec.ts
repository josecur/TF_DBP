/// <reference types="jasmine" />
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserProfileComponent } from './user-profile.component';
import { RouterTestingModule } from '@angular/router/testing'; // Para que soporte la inyección del Router

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;

  beforeEach(async () => {
    // 🚀 TRUCO DE INGENIERÍA: Simulamos un usuario en el localStorage para que el ngOnInit
    // del componente no lo rebote y deje inicializar la vista con éxito
    const fakeUser = {
      id: 'MND-TEST',
      nombres: 'Giancarlo',
      apellidos: 'Huisa',
      correo: 'test@mindstep.com',
      telefono: '999999999',
      ocupacion: 'EMPLEADO',
      sector: 'TECNOLOGÍA',
      modalidad: 'REMOTO',
      nivelCargaMental: 'MODERADO'
    };
    localStorage.setItem('usuario_mindstep', JSON.stringify(fakeUser));

    await TestBed.configureTestingModule({
      // Importamos el componente standalone y RouterTestingModule para manejar rutas falsas en testing
      imports: [UserProfileComponent, RouterTestingModule] 
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Limpieza al terminar las pruebas
  afterEach(() => {
    localStorage.removeItem('usuario_mindstep');
  });

  it('should create', () => {
    // El expect nativo de Jasmine ahora sí funcionará correctamente
    expect(component).toBeTruthy();
  });
});

