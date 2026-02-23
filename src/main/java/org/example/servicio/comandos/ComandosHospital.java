package org.example.servicio.comandos;

import static org.example.Main.SCANNER;
import static org.example.servicio.Utils.parseLongOrNull;

import java.util.Optional;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.repositorios.HospitalRepository;

public class ComandosHospital {

  private static ComandosHospital instance;

  //TODO refactorizar para eliminar dependencia circular entre servicios
  private final HospitalRepository hospitalRepository = HospitalRepository.getInstance();
  private final ComandosDepartamentos departamentosService = ComandosDepartamentos.getInstance();
  private static final String MENU_TITULO = "\n=== Gestion de Hospitales ===";
  private static final String SEPARADOR = "-------------------------";

  private ComandosHospital() {}

  public static ComandosHospital getInstance() {
    if (instance == null) {
      instance = new ComandosHospital();
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
          agregarDepartamento();
          break;
        case "4":
          verDepartamentos();
          break;
        case "5":
          crearSala();
          break;
        case "6":
          verDepartamentos();
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
    hospitalRepository.create(nuevoHospital);
    System.out.println("\nHospital creado exitosamente: " + nuevoHospital.getId());
  }

  private void verHospitales() {
    System.out.println("\n=== Lista de Hospitales ===\n");
    hospitalRepository.findAll().forEach(hospital -> {
      System.out.println("ID: " + hospital.getId());
      System.out.println("Nombre: " + hospital.getNombre());
      System.out.println("Direccion: " + hospital.getDireccion());
      System.out.println("Telefono: " + hospital.getTelefono());
      System.out.println(SEPARADOR);
    });
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
      Departamento departamento = departamentosService.crearDepartamento();
      nuevoHospital.agregarDepartamento(departamento);
    }

    return nuevoHospital;
  }

  private void agregarDepartamento() {
    Hospital hospital = seleccionarHospital();
    Departamento departamento = departamentosService.crearDepartamento();
    hospital.agregarDepartamento(departamento);

    Hospital creado = hospitalRepository.update(hospital);
    System.out.println("\nDepartamento: " + departamento.getNombre() + "creado exitosamente en el hospital " + creado.getNombre());
  }

  private Hospital seleccionarHospital() {
    System.out.println("\n\nSeleccione el ID del hospital al que desea agregar un departamento:");
    while (true) {
      imprimirHospitalesParaSeleccion();
      System.out.print("\nhospital > ");
      String hospitalId = SCANNER.nextLine().trim();
      Long id = parseLongOrNull(hospitalId);
      if (id == null) {
        System.out.println("ID invalido. Intente nuevamente.");
        continue;
      }
      Optional<Hospital> hospitalEncontrado = hospitalRepository.findByIdWithDepartamentos(id);
      if (hospitalEncontrado.isEmpty()) {
        System.out.println("Hospital no encontrado. Intente nuevamente.\n");
        continue;
      }
      return hospitalEncontrado.get();
    }
  }

  private void verDepartamentos() {
    Hospital hospital = seleccionarHospital();
    hospital = hospitalRepository.findByIdWithDepartamentosDetalles(hospital.getId()).orElse(null);
    if (hospital == null) {
      System.out.println("Hospital no encontrado. Intente nuevamente.");
      return;
    }
    departamentosService.mostrarDepartamentos(hospital);
  }

  private void imprimirHospitalesParaSeleccion() {
    hospitalRepository.findAll().forEach(h -> {
      System.out.println("ID: " + h.getId() + " - Nombre: " + h.getNombre());
    });
  }

  private void crearSala() {
    Hospital hospital = seleccionarHospital();
    departamentosService.agregarSala(hospital);
  }

  private void verSalas() {
    Hospital hospital = seleccionarHospital();
    hospital = hospitalRepository.findByIdWithDepartamentosDetalles(hospital.getId()).orElse(null);
    if (hospital == null) {
      System.out.println("Hospital no encontrado. Intente nuevamente.");
      return;
    }
    departamentosService.mostrarSalas(hospital);
  }
}
