package org.example.servicio.comandos;

import static org.example.Main.SCANNER;
import static org.example.servicio.Utils.parseLongOrNull;

import java.util.List;
import java.util.Optional;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.entidades.Sala;
import org.example.entidades.enums.EspecialidadMedico;
import org.example.repositorios.DepartamentoRepository;

public class ComandosDepartamentos {

  private static ComandosDepartamentos instance;

  private final ComandosHospital hospitalService = ComandosHospital.getInstance();
  private final ComandosSala salaService = ComandosSala.getInstance();
  private final DepartamentoRepository deptoRepository = DepartamentoRepository.getInstance();

  private ComandosDepartamentos() {}

  public static ComandosDepartamentos getInstance() {
    if (instance == null) {
      instance = new ComandosDepartamentos();
    }
    return instance;
  }

  public Departamento crearDepartamento() {
    return crearDepartamentoInteractive();
  }

  private Departamento crearDepartamentoInteractive() {
    System.out.println("\n=== Formulario de creacion de departamento ===");
    System.out.print("Nombre del departamento: ");
    String nombre = SCANNER.nextLine().trim();

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

    return Departamento.builder().nombre(nombre).especialidad(especialidad).build();
  }

  public void mostrarDepartamentos(Hospital hospital) {
    System.out.println("=== Departamentos del Hospital " + hospital.getNombre() + " ===");
    hospital.getDepartamentos().forEach(departamento -> {
      System.out.println("ID: " + departamento.getId());
      System.out.println("Nombre: " + departamento.getNombre());
      System.out.println("Especialidad: " + departamento.getEspecialidad());
      System.out.println("Cantidad de medicos: " + departamento.getMedicos().size());
      System.out.println("Cantidad de salas: " + departamento.getSalas().size());
      System.out.println("-------------------------");
    });
  }

  public void mostrarMenuDepartamentos(Hospital hospital) {
    while (true) {
      System.out.println("\n=== Menu de Departamentos del Hospital " + hospital.getNombre() + " ===");
      System.out.println("1. Ver departamentos");
      System.out.println("2. Agregar departamento");
      System.out.println("3. Agregar sala");
      System.out.println("3. Volver al menu principal");
      System.out.print("Seleccione una opcion > ");
      String opcion = SCANNER.nextLine().trim();

      switch (opcion) {
        case "1":
          mostrarDepartamentos(hospital);
          break;
        case "2":
          hospitalService.agregarDepartamento(hospital);
          break;
        case "3":
          agregarSala(hospital);
          break;
        case "4":
          return;
        default:
          System.out.println("Opcion no valida, intente nuevamente.");
      }
    }
  }

  private void agregarSala(Hospital hospital) {
    Sala nuevaSala = salaService.crearSala();

    Departamento deptoSeleccionado = seleccionarDepartamento(hospital);
    deptoSeleccionado.agregarSala(nuevaSala);

    hospitalService.actualizarHospital(hospital);
    System.out.println("\nSala agregada exitosamente al departamento " + deptoSeleccionado.getNombre());
  }

  private Departamento seleccionarDepartamento(Hospital hospital) {
    System.out.println("\n\nSeleccione el ID del departamento al que desea agregar una sala:");
    List<Departamento> deptos = deptoRepository.findByHospital(hospital.getId());
    while (true) {
      imprimirDepartamentosParaSeleccion(deptos);
      System.out.print("\ndepartamento > ");
      String deptoId = SCANNER.nextLine().trim();
      Long id = parseLongOrNull(deptoId);
      if (id == null) {
        System.out.println("ID invalido. Intente nuevamente.");
        continue;
      }
      Departamento deptoSeleccionado = deptos.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
      if (deptoSeleccionado == null) {
        System.out.println("Departamento no encontrado. Intente nuevamente.\n");
        continue;
      }
      return deptoSeleccionado;
    }
  }

  private void imprimirDepartamentosParaSeleccion(List<Departamento> deptos) {
    deptos.forEach(h -> System.out.println("ID: " + h.getId() + " - Nombre: " + h.getNombre()));
  }
}
