package org.example.ui;

import static org.example.Main.SCANNER;
import static org.example.utils.Utils.parseLongOrNull;

import java.util.List;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.servicio.HospitalService;

public class HospitalUI {

  private static HospitalUI instance;

  private final HospitalService hospitalService = HospitalService.getInstance();
  private final DepartamentoUI departamentoUI = DepartamentoUI.getInstance();

  private static final String MENU_TITULO = "\n=== Gestion de Hospitales ===";
  private static final String SEPARADOR = "-------------------------";

  private HospitalUI() {}

  public static HospitalUI getInstance() {
    if (instance == null) {
      instance = new HospitalUI();
    }
    return instance;
  }

  public void gestionarHospital() {
    while (true) {
    mostrarMenu();
      String opcion = SCANNER.nextLine().trim();
      if (opcion.equalsIgnoreCase("7")) {
        break;
      }

      switch (opcion) {
        case "1":
          crearHospital();
          break;
        case "2":
          verHospitales();
          break;
        case "3":
          crearDepartamento();
          break;
        case "4":
          verDepartamentos();
          break;
        case "5":
          crearSala();
          break;
        case "6":
          verSalas();
          break;
        default:
          System.out.println("Opcion no valida. Intente nuevamente.");
      }
    }
  }

  private void mostrarMenu() {
    System.out.println(MENU_TITULO);
    System.out.println("1. Crear hospital");
    System.out.println("2. Ver Hospitales");
    System.out.println("3. Crear departamento");
    System.out.println("4. Ver departamentos");
    System.out.println("5. Crear sala");
    System.out.println("6. Ver salas");
    System.out.println("7. Volver al menu principal");
    System.out.print("\nopcion > ");
  }

  private void crearHospital() {
    Hospital nuevoHospital = ingresarDatosHospital();
    try {
      hospitalService.crearHospital(nuevoHospital);
      System.out.println("\nHospital creado exitosamente.");
    } catch (Exception e) {
      System.out.println("Error al crear el hospital: " + e.getMessage());
    }
  }

  private Hospital ingresarDatosHospital() {
    System.out.println("\n=== Formulario de creacion de hospital ===");
    System.out.print("Nombre del hospital: ");
    String nombre = SCANNER.nextLine().trim();

    System.out.print("Direccion: ");
    String direccion = SCANNER.nextLine().trim();

    System.out.print("Telefono: ");
    String telefono = SCANNER.nextLine().trim();

    Hospital nuevoHospital = Hospital.builder()
        .nombre(nombre)
        .direccion(direccion)
        .telefono(telefono)
        .build();

    System.out.print("Desea agregar un departamento al hospital? s/n > ");
    String agregarDepto = SCANNER.nextLine().trim().toLowerCase();
    if (agregarDepto.equals("s")) {
      Departamento departamento = departamentoUI.crearDepartamento();
      nuevoHospital.agregarDepartamento(departamento);
    }

    return nuevoHospital;
  }

  private boolean verHospitales() {
    System.out.println("\n=== Lista de Hospitales ===\n");
    List<Hospital> hospitales = hospitalService.obtenerTodos();
    if (hospitales.isEmpty()) {
      System.out.println("No hay hospitales registrados.");
      return false;
    }

    hospitales.forEach(hospital -> {
      System.out.println("ID: " + hospital.getId());
      System.out.println("Nombre: " + hospital.getNombre());
      System.out.println("Direccion: " + hospital.getDireccion());
      System.out.println(SEPARADOR);
    });
    return true;
  }

  private void crearDepartamento() {
    Long hospitalId = seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    Departamento departamento = departamentoUI.crearDepartamento();

    try {
      hospitalService.crearDepartamento(hospitalId, departamento);
      System.out.println("\nDepartamento creado exitosamente en el hospital.");
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void verDepartamentos() {
    Long hospitalId = seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    try {
      departamentoUI.verDepartamentos(hospitalId);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public Long seleccionarIdHospital() {
    System.out.println("\n\nSeleccione el ID del hospital:");
    boolean hayHospitales = verHospitales();
    if (!hayHospitales) {
      return null;
    }

    System.out.print("\nhospital > ");
    return parseLongOrNull(SCANNER.nextLine().trim());
  }

  private void crearSala() {
    Long hospitalId = seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    departamentoUI.crearSala(hospitalId);
  }

  private void verSalas() {
    Long hospitalId = seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    departamentoUI.verSalas(hospitalId);
  }
}
