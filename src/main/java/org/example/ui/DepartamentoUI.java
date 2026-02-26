package org.example.ui;

import static org.example.Main.SCANNER;
import static org.example.utils.Utils.parseLongOrNull;

import java.util.List;
import org.example.config.JpaUtil;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.entidades.Sala;
import org.example.entidades.enums.EspecialidadMedico;
import org.example.repositorios.DepartamentoRepository;
import org.example.servicio.DepartamentoService;

public class DepartamentoUI {

  private static DepartamentoUI instance;

  private final DepartamentoService departamentoService = DepartamentoService.getInstance();
  private final SalaUI salaUI = SalaUI.getInstance();

  private DepartamentoUI() {}

  public static DepartamentoUI getInstance() {
    if (instance == null) {
      instance = new DepartamentoUI();
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

  public boolean verDepartamentos(Long hospitalId) {
    List<Departamento> departamentos = departamentoService.obtenerTodos(hospitalId);
    if (departamentos.isEmpty()) {
      System.out.println("No hay departamentos registrados para este hospital.");
      return false;
    }

    System.out.println("\n=== Lista de Departamentos ===\n");
    departamentos.forEach(departamento -> {
      System.out.println("ID: " + departamento.getId());
      System.out.println("Nombre: " + departamento.getNombre());
      System.out.println("Especialidad: " + departamento.getEspecialidad());
      System.out.println("-------------------------");
    });

    return true;
  }

  public void crearSala(Long hospitalId) {
    Long departamentoId = seleccionarIdDepartamento(hospitalId);
    if (departamentoId == null) {
      return;
    }

    Sala sala = salaUI.crearSala();

    try {
      departamentoService.crearSala(departamentoId, sala);
      System.out.println("\nSala creada exitosamente en el hospital.");
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public void verSalas(Long hospitalId) {
    Long departamentoId = seleccionarIdDepartamento(hospitalId);
    if (departamentoId == null) {
      return;
    }

    try {
      salaUI.verSalas(departamentoId);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public Long seleccionarIdDepartamento(Long hospitalId) {
    System.out.println("\n\nSeleccione el ID del departamento:");
    boolean hayDepartamentos = verDepartamentos(hospitalId);
    if (!hayDepartamentos) {
      return null;
    }

    System.out.print("\ndepartamento > ");
    return parseLongOrNull(SCANNER.nextLine().trim());
  }
}
