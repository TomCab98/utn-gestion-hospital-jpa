package org.example.ui;

import static org.example.Main.SCANNER;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;
import org.example.excepciones.CitaException;
import org.example.servicio.CitaManager;
import org.example.servicio.MedicoService;
import org.example.servicio.PacienteService;
import org.example.servicio.SalaService;

public class CitaUI {

  private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private static CitaUI instance;

  private final CitaManager citaManager = CitaManager.getInstance();
  private final HospitalUI hospitalUI = HospitalUI.getInstance();
  private final DepartamentoUI departamentoUI = DepartamentoUI.getInstance();
  private final PacienteService pacienteService = PacienteService.getInstance();
  private final MedicoService medicoService = MedicoService.getInstance();
  private final SalaService salaService = SalaService.getInstance();

  private CitaUI() {}

  public static CitaUI getInstance() {
    if (instance == null) {
      instance = new CitaUI();
    }
    return instance;
  }

  public void programarCita() {
    Long hospitalId = hospitalUI.seleccionarIdHospital();
    if (hospitalId == null) {
      return;
    }

    List<Paciente> pacientes = pacienteService.obtenerPorHospital(hospitalId);
    if (pacientes.isEmpty()) {
      System.out.println("No hay pacientes registrados para este hospital.");
      return;
    }

    Long departamentoId = departamentoUI.seleccionarIdDepartamento(hospitalId);
    if (departamentoId == null) {
      return;
    }

    List<Medico> medicos = medicoService.obtenerPorDepartamento(departamentoId);
    if (medicos.isEmpty()) {
      System.out.println("No hay medicos registrados para este departamento.");
      return;
    }

    List<Sala> salas = salaService.obtenerTodos(departamentoId);
    if (salas.isEmpty()) {
      System.out.println("No hay salas registradas para este departamento.");
      return;
    }

    Paciente paciente = seleccionarPaciente(pacientes);
    if (paciente == null) {
      return;
    }

    Medico medico = seleccionarMedico(medicos);
    if (medico == null) {
      return;
    }

    Sala sala = seleccionarSala(salas);
    if (sala == null) {
      return;
    }

    LocalDateTime fechaHora = ingresarFechaHora();
    BigDecimal costo = ingresarCosto();

    try {
      Cita cita = citaManager.programarCita(paciente, medico, sala, fechaHora, costo);
      System.out.println("\nCita programada exitosamente.");
      System.out.println(cita);
    } catch (CitaException e) {
      System.out.println("Error al programar la cita: " + e.getMessage());
    }
  }

  private Paciente seleccionarPaciente(List<Paciente> pacientes) {
    System.out.println("\nSeleccione paciente por indice:");
    for (int i = 0; i < pacientes.size(); i++) {
      Paciente p = pacientes.get(i);
      System.out.println("  [" + i + "] " + p.getNombre() + " " + p.getApellido() + " - DNI: " + p.getDni());
    }
    System.out.print("\npaciente > ");
    Integer idx = parseIndice(pacientes.size());
    return idx == null ? null : pacientes.get(idx);
  }

  private Medico seleccionarMedico(List<Medico> medicos) {
    System.out.println("\nSeleccione medico por indice:");
    for (int i = 0; i < medicos.size(); i++) {
      Medico m = medicos.get(i);
      System.out.println("  [" + i + "] " + m.getNombre() + " " + m.getApellido()
          + " - Matricula: " + (m.getMatricula() != null ? m.getMatricula().getMatricula() : "n/a"));
    }
    System.out.print("\nmedico > ");
    Integer idx = parseIndice(medicos.size());
    return idx == null ? null : medicos.get(idx);
  }

  private Sala seleccionarSala(List<Sala> salas) {
    System.out.println("\nSeleccione sala por indice:");
    for (int i = 0; i < salas.size(); i++) {
      Sala s = salas.get(i);
      System.out.println("  [" + i + "] " + s.getNumero() + " - Tipo: " + s.getTipo());
    }
    System.out.print("\nsala > ");
    Integer idx = parseIndice(salas.size());
    return idx == null ? null : salas.get(idx);
  }

  private LocalDateTime ingresarFechaHora() {
    while (true) {
      try {
        System.out.print("Fecha y hora (yyyy-MM-dd HH:mm): ");
        return LocalDateTime.parse(SCANNER.nextLine().trim(), DATE_TIME_FMT);
      } catch (Exception e) {
        System.out.println("Fecha/hora no valida, intente nuevamente.");
      }
    }
  }

  private BigDecimal ingresarCosto() {
    while (true) {
      try {
        System.out.print("Valor (ej. 150000.00): ");
        BigDecimal costo = new BigDecimal(SCANNER.nextLine().trim());
        if (costo.compareTo(BigDecimal.ZERO) <= 0) {
          System.out.println("El costo debe ser mayor a cero.");
          continue;
        }
        return costo;
      } catch (Exception e) {
        System.out.println("Valor no valido, intente nuevamente.");
      }
    }
  }

  private Integer parseIndice(int size) {
    try {
      int idx = Integer.parseInt(SCANNER.nextLine().trim());
      if (idx < 0 || idx >= size) {
        System.out.println("Indice fuera de rango.");
        return null;
      }
      return idx;
    } catch (Exception e) {
      System.out.println("Indice no valido.");
      return null;
    }
  }
}
