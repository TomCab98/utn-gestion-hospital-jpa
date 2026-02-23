package org.example.servicio.comandos;

import static org.example.Main.SCANNER;
import static org.example.servicio.Utils.parseLongOrNull;

import java.util.List;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.entidades.Sala;
import org.example.entidades.enums.EspecialidadMedico;
import org.example.repositorios.DepartamentoRepository;

public class ComandosDepartamentos {

  private static ComandosDepartamentos instance;

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
    return ingresarDatosDepartamento();
  }

  private Departamento ingresarDatosDepartamento() {
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

  public void agregarSala(Hospital hospital) {
    Departamento deptoSeleccionado = seleccionarDepartamento(hospital);

    Sala nuevaSala = salaService.crearSala();
    deptoSeleccionado.agregarSala(nuevaSala);

    deptoRepository.update(deptoSeleccionado);
    System.out.println("\nSala agregada exitosamente al departamento " + deptoSeleccionado);
  }

  private Departamento seleccionarDepartamento(Hospital hospital) {
    System.out.println("\n\nSeleccione el ID del departamento al que desea agregar una sala:");
    List<Departamento> deptos = deptoRepository.findByHospitalWithSalas(hospital.getId());
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

  public void mostrarSalas(Hospital hospital) {
    Departamento deptoSeleccionado = seleccionarDepartamento(hospital);
    System.out.println("=== Salas del Departamento " + deptoSeleccionado.getNombre() + " ===");
    salaService.mostrarSalas(deptoSeleccionado);
  }
}
