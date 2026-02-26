package org.example.ui;

import static org.example.Main.SCANNER;

import java.time.LocalDate;
import java.util.Locale;
import org.example.entidades.Paciente;
import org.example.entidades.enums.TipoSangre;
import org.example.servicio.PacienteService;

public class PacienteUI {

  private static PacienteUI instance;

  private final PacienteService pacienteService = PacienteService.getInstance();
  private final HospitalUI hospitalUI = HospitalUI.getInstance();
  private final DepartamentoUI departamentoUI = DepartamentoUI.getInstance();

  private PacienteUI() {}

  public static PacienteUI getInstance() {
    if (instance == null) {
      instance = new PacienteUI();
    }
    return instance;
  }

  public void gestionarPacientes() {
    while (true) {
      mostrarMenu();
      String opcion = SCANNER.nextLine().trim();
      if (opcion.equalsIgnoreCase("3")) {
        break;
      }

      switch (opcion) {
        case "1":
          crearPaciente();
          break;
        case "2":
          mostrarPacientesPorHospital();
          break;
        default:
          System.out.println("Opcion no valida. Intente nuevamente.");
      }
    }
  }

  private void mostrarMenu() {
    System.out.println("\n=== Gestion de Pacientes ===");
    System.out.println("1. Crear paciente");
    System.out.println("2. Ver pacientes");
    System.out.println("3. Volver al menu principal");
    System.out.print("\nopcion > ");
  }

  private void crearPaciente() {
    Long hospitalId = hospitalUI.seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    Paciente nuevoPaciente = ingresarDatosPaciente();
    try {
      pacienteService.crearPaciente(hospitalId, nuevoPaciente);
      System.out.println("\nPaciente creado exitosamente.");
    } catch (Exception e) {
      System.out.println("Error al crear paciente: " + e.getMessage());
    }
  }

  private static Paciente ingresarDatosPaciente() {
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

    System.out.print("Teléfono: ");
    String telefono = SCANNER.nextLine().trim();

    System.out.print("Dirección: ");
    String direccion = SCANNER.nextLine().trim();

    Paciente p = Paciente.builder()
        .nombre(nombre)
        .apellido(apellido)
        .dni(dni)
        .fechaNacimiento(fn)
        .tipoSangre(tipoSangre)
        .telefono(telefono)
        .direccion(direccion)
        .build();

    System.out.print("Agregar diagnóstico inicial (enter para omitir): ");
    String diag = SCANNER.nextLine().trim();
    if (!diag.isEmpty()) {
      p.agregarDiagnostico(diag);
    }

    System.out.print("Agregar tratamiento inicial (enter para omitir): ");
    String tr = SCANNER.nextLine().trim();
    if (!tr.isEmpty()) {
      p.agregarTratamiento(tr);
    }

    System.out.print("Agregar alergia (enter para omitir): ");
    String al = SCANNER.nextLine().trim();
    if (!al.isEmpty()) {
      p.agregarAlergia(al);
    }

    return p;
  }

  private void mostrarPacientesPorHospital() {
    Long hospitalId = hospitalUI.seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    verPacientes(hospitalId);
  }

  private void verPacientes(Long hospitalId) {
    System.out.println("\n=== Lista de Pacientes ===\n");
    pacienteService.obtenerPorHospital(hospitalId).forEach(paciente -> {
      System.out.println("Nombre: " + paciente.getNombre() + " " + paciente.getApellido());
      System.out.println("DNI: " + paciente.getDni());
      System.out.println("Telefono: " + paciente.getTelefono());
      System.out.println("Direccion: " + paciente.getDireccion());
      System.out.println("Fecha de nacimiento: " + paciente.getFechaNacimiento());
      System.out.println("Tipo de sangre: " + paciente.getTipoSangre());
      System.out.println("--------------------------");
    });
  }
}
