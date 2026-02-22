package org.example;

import static org.example.repositorios.RepositoryManager.mergeEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import org.example.entidades.Cita;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.entidades.Matricula;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;
import org.example.entidades.enums.EspecialidadMedico;
import org.example.entidades.enums.EstadoCita;
import org.example.entidades.enums.TipoSala;
import org.example.entidades.enums.TipoSangre;
import org.example.excepciones.CitaException;
import org.example.servicio.CitaManager;
import org.example.servicio.comandos.ComandosHospital;

public class Main {

  private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  public static final Scanner SCANNER = new Scanner(System.in, StandardCharsets.UTF_8);

  private static final ComandosHospital comandosHospital = ComandosHospital.getInstance();

  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("hospital-persistence-unit");
    EntityManager em = emf.createEntityManager();

    Hospital currentHospital = null;
    final List<Departamento> departamentos = new ArrayList<>();
    final List<Medico> medicos = new ArrayList<>();
    final List<Paciente> pacientes = new ArrayList<>();
    final List<Sala> salas = new ArrayList<>();

    CitaManager citaManager = new CitaManager();

    try {
      printHelp();
      while (true) {
        System.out.print("\ncomando> ");
        String cmd = SCANNER.nextLine().trim();
        if (cmd.equalsIgnoreCase("exit")) {
          System.out.println("Saliendo...");
          break;
        }
        switch (cmd.toLowerCase(Locale.ROOT)) {
          case "help":
            printHelp();
            break;
          case "hospital":
            comandosHospital.gestionarHospital();
            break;
          case "create sala":
            if (departamentos.isEmpty()) {
              System.out.println("No hay departamentos. Cree uno con: create departamento");
              break;
            }
            Sala sala = createSalaInteractive(departamentos);
            salas.add(sala);
            // No se asegura persistencia directa: se hace merge del hospital para propagar si corresponde
            if (currentHospital != null) {
              mergeEntity(em, currentHospital);
            }
            System.out.println("Sala creada en memoria.");
            break;

          case "create medico":
            if (departamentos.isEmpty()) {
              System.out.println("No hay departamentos. Cree uno con: create departamento");
              break;
            }
            Medico medico = createMedicoInteractive(departamentos);
            // añadir al departamento correspondiente (se asume que el método agrega la relación)
            medicos.add(medico);
            if (currentHospital != null) {
              mergeEntity(em, currentHospital);
            }
            System.out.println("Médico creado en memoria.");
            break;

          case "create paciente":
            if (currentHospital == null) {
              System.out.println("Primero cree un hospital (comando: create hospital).");
              break;
            }
            Paciente paciente = createPacienteInteractive();
            currentHospital.agregarPaciente(paciente);
            pacientes.add(paciente);
            mergeEntity(em, currentHospital);
            System.out.println("Paciente creado y agregado al hospital.");
            break;

          case "programar cita":
            if (medicos.isEmpty() || pacientes.isEmpty() || salas.isEmpty()) {
              System.out.println("Asegúrese de tener al menos 1 médico, 1 paciente y 1 sala creados.");
              break;
            }
            try {
              Cita cita = scheduleCitaInteractive(citaManager, medicos, pacientes, salas);
              System.out.println("Cita programada (id: " + cita.getId() + ")");
            } catch (CitaException ex) {
              System.err.println("Error al programar cita: " + ex.getMessage());
            } catch (Exception ex) {
              System.err.println("Entrada inválida: " + ex.getMessage());
            }
            break;

          case "listar":
            runQueries(em);
            break;

          default:
            System.out.println("Comando desconocido. Escriba 'help' para ver comandos.");
        }
      }
    } finally {
      if (em.isOpen()) em.close();
      if (emf.isOpen()) emf.close();
      SCANNER.close();
    }
  }

  private static void printHelp() {
    System.out.println("Comandos disponibles:");
    System.out.println("  help               - Mostrar esta ayuda");
    System.out.println("  hospital           - Gestion de hospitales (crear, listar, etc)");
    System.out.println("  create departamento- Crear un departamento y asociarlo al hospital");
    System.out.println("  create sala        - Crear una sala y asociarla a un departamento");
    System.out.println("  create medico      - Crear un médico y asociarlo a un departamento");
    System.out.println("  create paciente    - Crear un paciente y asociarlo al hospital");
    System.out.println("  programar cita     - Programar una cita (usa CitaManager)");
    System.out.println("  listar             - Ejecutar consultas de ejemplo");
    System.out.println("  exit               - Terminar el servicio");
  }



  private static Sala createSalaInteractive(List<Departamento> departamentos) {
    System.out.print("Número de sala: ");
    String numero = SCANNER.nextLine().trim();
    System.out.print("Tipo de sala (CONSULTORIO, QUIRURGICO, etc): ");
    String tipoStr = SCANNER.nextLine().trim().toUpperCase(Locale.ROOT);
    TipoSala tipo;
    try {
      tipo = TipoSala.valueOf(tipoStr);
    } catch (Exception e) {
      tipo = TipoSala.CONSULTORIO;
    }

    System.out.println("Seleccione departamento por índice:");
    for (int i = 0; i < departamentos.size(); i++) {
      System.out.println("  [" + i + "] " + departamentos.get(i).getNombre());
    }
    int idx = Integer.parseInt(SCANNER.nextLine().trim());
    Departamento dep = departamentos.get(Math.max(0, Math.min(idx, departamentos.size() - 1)));

    return Sala.builder()
        .numero(numero)
        .tipo(tipo)
        .departamento(dep)
        .build();
  }

  private static Medico createMedicoInteractive(List<Departamento> departamentos) {
    System.out.print("Nombre: ");
    String nombre = SCANNER.nextLine().trim();
    System.out.print("Apellido: ");
    String apellido = SCANNER.nextLine().trim();
    System.out.print("DNI: ");
    String dni = SCANNER.nextLine().trim();
    System.out.print("Fecha de nacimiento (yyyy-MM-dd): ");
    LocalDate fn = LocalDate.parse(SCANNER.nextLine().trim());
    System.out.print("Tipo de sangre (A_POSITIVO, O_NEGATIVO, ...): ");
    TipoSangre ts;
    try {
      ts = TipoSangre.valueOf(SCANNER.nextLine().trim().toUpperCase(Locale.ROOT));
    } catch (Exception e) {
      ts = TipoSangre.A_POSITIVO;
    }
    System.out.print("Matrícula: ");
    String mat = SCANNER.nextLine().trim();
    System.out.print("Especialidad (CARDIOLOGIA, PEDIATRIA, ...): ");
    EspecialidadMedico esp;
    try {
      esp = EspecialidadMedico.valueOf(SCANNER.nextLine().trim().toUpperCase(Locale.ROOT));
    } catch (Exception e) {
      esp = EspecialidadMedico.CARDIOLOGIA;
    }

    Medico medico = Medico.builder()
        .nombre(nombre)
        .apellido(apellido)
        .dni(dni)
        .fechaNacimiento(fn)
        .tipoSangre(ts)
        .matricula(Matricula.of(mat))
        .especialidad(esp)
        .build();

    System.out.println("Seleccione departamento por índice para asociar el médico:");
    for (int i = 0; i < departamentos.size(); i++) {
      System.out.println("  [" + i + "] " + departamentos.get(i).getNombre());
    }
    int idx = Integer.parseInt(SCANNER.nextLine().trim());
    Departamento dep = departamentos.get(Math.max(0, Math.min(idx, departamentos.size() - 1)));
    dep.agregarMedico(medico);

    return medico;
  }

  private static Paciente createPacienteInteractive() {
    System.out.print("Nombre: ");
    String nombre = SCANNER.nextLine().trim();
    System.out.print("Apellido: ");
    String apellido = SCANNER.nextLine().trim();
    System.out.print("DNI: ");
    String dni = SCANNER.nextLine().trim();
    System.out.print("Fecha de nacimiento (yyyy-MM-dd): ");
    LocalDate fn = LocalDate.parse(SCANNER.nextLine().trim());
    System.out.print("Tipo de sangre (A_POSITIVO, O_NEGATIVO, ...): ");
    TipoSangre ts;
    try {
      ts = TipoSangre.valueOf(SCANNER.nextLine().trim().toUpperCase(Locale.ROOT));
    } catch (Exception e) {
      ts = TipoSangre.A_POSITIVO;
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
        .tipoSangre(ts)
        .telefono(telefono)
        .direccion(direccion)
        .build();

    System.out.print("Agregar diagnóstico inicial (enter para omitir): ");
    String diag = SCANNER.nextLine().trim();
    if (!diag.isEmpty()) p.agregarDiagnostico(diag);
    System.out.print("Agregar tratamiento inicial (enter para omitir): ");
    String tr = SCANNER.nextLine().trim();
    if (!tr.isEmpty()) p.agregarTratamiento(tr);
    System.out.print("Agregar alergia (enter para omitir): ");
    String al = SCANNER.nextLine().trim();
    if (!al.isEmpty()) p.agregarAlergia(al);

    return p;
  }

  private static Cita scheduleCitaInteractive(CitaManager citaManager,
      List<Medico> medicos,
      List<Paciente> pacientes,
      List<Sala> salas) throws CitaException {
    System.out.println("Seleccione paciente por índice:");
    for (int i = 0; i < pacientes.size(); i++) {
      Paciente p = pacientes.get(i);
      System.out.println("  [" + i + "] " + p.getNombre() + " " + p.getApellido());
    }
    int pIdx = Integer.parseInt(SCANNER.nextLine().trim());

    System.out.println("Seleccione médico por índice:");
    for (int i = 0; i < medicos.size(); i++) {
      Medico m = medicos.get(i);
      System.out.println("  [" + i + "] " + m.getNombre() + " " + m.getApellido());
    }
    int mIdx = Integer.parseInt(SCANNER.nextLine().trim());

    System.out.println("Seleccione sala por índice:");
    for (int i = 0; i < salas.size(); i++) {
      Sala s = salas.get(i);
      System.out.println("  [" + i + "] " + s.getNumero());
    }
    int sIdx = Integer.parseInt(SCANNER.nextLine().trim());

    System.out.print("Fecha y hora (yyyy-MM-dd HH:mm): ");
    LocalDateTime fecha = LocalDateTime.parse(SCANNER.nextLine().trim(), DATE_TIME_FMT);
    System.out.print("Valor (ej. 150000.00): ");
    BigDecimal valor = new BigDecimal(SCANNER.nextLine().trim());

    Paciente paciente = pacientes.get(Math.max(0, Math.min(pIdx, pacientes.size() - 1)));
    Medico medico = medicos.get(Math.max(0, Math.min(mIdx, medicos.size() - 1)));
    Sala sala = salas.get(Math.max(0, Math.min(sIdx, salas.size() - 1)));

    return citaManager.programarCita(paciente, medico, sala, fecha, valor);
  }

  private static void runQueries(EntityManager em) {
    TypedQuery<Medico> query = em.createQuery(
        "SELECT m FROM Medico m WHERE m.especialidad = :esp",
        Medico.class);
    query.setParameter("esp", EspecialidadMedico.CARDIOLOGIA);
    List<Medico> cardiologos = query.getResultList();

    Long citasCompletadas = em.createQuery(
            "SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado",
            Long.class)
        .setParameter("estado", EstadoCita.COMPLETADA)
        .getSingleResult();

    TypedQuery<Paciente> queryAlergicos = em.createQuery(
        "SELECT DISTINCT p FROM Paciente p JOIN p.historiaClinica h WHERE SIZE(h.alergias) > 0",
        Paciente.class);

    System.out.println("Lista de cardiologos: " + cardiologos);
    System.out.println("Citas completadas: " + citasCompletadas);
    System.out.println("Pacientes con alergias: " + queryAlergicos.getResultList());
  }
}
