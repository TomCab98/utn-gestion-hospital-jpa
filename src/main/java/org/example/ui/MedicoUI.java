package org.example.ui;

import static org.example.Main.SCANNER;

import java.time.LocalDate;
import org.example.entidades.Matricula;
import org.example.entidades.Medico;
import org.example.entidades.enums.EspecialidadMedico;
import org.example.entidades.enums.TipoSangre;
import org.example.servicio.MedicoService;

public class MedicoUI {

  private static MedicoUI instance;

  private final MedicoService medicoService = MedicoService.getInstance();
  private final HospitalUI hospitalUI = HospitalUI.getInstance();
  private final DepartamentoUI departamentoUI = DepartamentoUI.getInstance();

  private MedicoUI() {}

  public static MedicoUI getInstance() {
    if (instance == null) {
      instance = new MedicoUI();
    }
    return instance;
  }

  public void gestionarMedicos() {
    while (true) {
      mostrarMenu();
      String opcion = SCANNER.nextLine().trim();
      if (opcion.equalsIgnoreCase("3")) {
        break;
      }

      switch (opcion) {
        case "1":
          crearMedico();
          break;
        case "2":
          verMedicos();
          break;
        default:
          System.out.println("Opcion no valida. Intente nuevamente.");
      }
    }
  }

  private void mostrarMenu() {
    System.out.println("\n=== Gestion de Medicos ===");
    System.out.println("1. Crear medico");
    System.out.println("2. Ver medicos");
    System.out.println("3. Volver al menu principal");
    System.out.print("\nopcion > ");
  }

  private void crearMedico() {
    Long hospitalId = hospitalUI.seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    Long departamentoId = departamentoUI.seleccionarIdDepartamento(hospitalId);
    if (departamentoId == null) {
      return;
    }

    Medico nuevoMedico = ingresarDatosMedico();
    try {
      medicoService.crearMedico(departamentoId, nuevoMedico);
      System.out.println("\nMedico creado exitosamente.");
    } catch (Exception e) {
      System.out.println("Error al crear medico: " + e.getMessage());
    }
  }

  private Medico ingresarDatosMedico() {
    System.out.println("\n=== Formulario de creacion de medico ===");

    System.out.print("Nombre: ");
    String nombre = SCANNER.nextLine().trim();

    System.out.print("Apellido: ");
    String apellido = SCANNER.nextLine().trim();

    System.out.print("DNI: ");
    String dni = SCANNER.nextLine().trim();

    LocalDate fn = null;
    boolean fechaValida = false;
    while (!fechaValida) {
      try {
        System.out.print("Fecha de nacimiento (yyyy-MM-dd): ");
        fn = LocalDate.parse(SCANNER.nextLine().trim());
        fechaValida = true;
      } catch (Exception e) {
        System.out.println("Fecha no valida, intente nuevamente.");
      }
    }

    System.out.print("Matricula (MP-XXXXX)ex"
        + ""
        + ": ");
    String mat = SCANNER.nextLine().trim();

    TipoSangre tipoSangre = null;
    boolean tipoSeleccionado = false;
    while (!tipoSeleccionado) {
      System.out.println("Seleccione un tipo de sangre: ");
      for (int i = 0; i < TipoSangre.values().length; i++) {
        System.out.println((i + 1) + ". " + TipoSangre.values()[i].name());
      }
      System.out.print("numero > ");
      String esp = SCANNER.nextLine().trim();
      try {
        tipoSangre = TipoSangre.values()[Integer.parseInt(esp) - 1];
        tipoSeleccionado = true;
      } catch (Exception e) {
        System.out.println("Tipo de sangre no valido, intente nuevamente.");
      }
    }

    EspecialidadMedico especialidad = null;
    boolean especialidadSeleccionada = false;
    while (!especialidadSeleccionada) {
      System.out.println("Seleccione especialidad: ");
      for (int i = 0; i < EspecialidadMedico.values().length; i++) {
        System.out.println((i + 1) + ". " + EspecialidadMedico.values()[i].name());
      }
      System.out.print("numero > ");
      String esp = SCANNER.nextLine().trim();
      try {
        especialidad = EspecialidadMedico.values()[Integer.parseInt(esp) - 1];
        especialidadSeleccionada = true;
      } catch (Exception e) {
        System.out.println("Especialidad no valida, intente nuevamente.");
      }
    }

    return Medico.builder()
        .nombre(nombre)
        .apellido(apellido)
        .dni(dni)
        .fechaNacimiento(fn)
        .tipoSangre(tipoSangre)
        .matricula(Matricula.of(mat))
        .especialidad(especialidad)
        .build();
  }

  private void verMedicos() {
    System.out.println("\n=== Lista de Medicos ===\n");
    medicoService.obtenerTodos().forEach(medico -> {
      System.out.println("Matricula: " + medico.getMatricula().getMatricula());
      System.out.println("Nombre: " + medico.getNombre() + " " + medico.getApellido());
      System.out.println("DNI: " + medico.getDni());
      System.out.println("Fecha de nacimiento: " + medico.getFechaNacimiento());
      System.out.println("Tipo de sangre: " + medico.getTipoSangre());
      System.out.println("Especialidad: " + medico.getEspecialidad());
      System.out.println("--------------------------");
    });
  }
}
