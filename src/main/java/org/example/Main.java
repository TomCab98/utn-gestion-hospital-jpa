package org.example;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;
import org.example.ui.CitaUI;
import org.example.ui.HospitalUI;
import org.example.ui.MedicoUI;
import org.example.ui.PacienteUI;

public class Main {

  public static final Scanner SCANNER = new Scanner(System.in, StandardCharsets.UTF_8);

  private static final HospitalUI HOSPITAL_UI = HospitalUI.getInstance();
  private static final MedicoUI MEDICO_UI = MedicoUI.getInstance();
  private static final PacienteUI PACIENTE_UI = PacienteUI.getInstance();
  private static final CitaUI CITA_UI = CitaUI.getInstance();

  public static void main(String[] args) {
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
            HOSPITAL_UI.gestionarHospital();
            break;
          case "medico":
            MEDICO_UI.gestionarMedicos();
            break;
          case "paciente":
            PACIENTE_UI.gestionarPacientes();
            break;
          case "programar cita":
            CITA_UI.programarCita();
            break;
          default:
            System.out.println("Comando desconocido. Escriba 'help' para ver comandos.");
        }
      }
    } finally {
      SCANNER.close();
    }
  }

  private static void printHelp() {
    System.out.println("Comandos disponibles:");
    System.out.println("  help               - Mostrar esta ayuda");
    System.out.println("  hospital           - Gestion de hospitales (crear, listar, etc)");
    System.out.println("  medico             - Crear un medico y asociarlo a un departamento");
    System.out.println("  paciente           - Crear un paciente y asociarlo al hospital");
    System.out.println("  programar cita     - Programar una cita");
    System.out.println("  exit               - Terminar el servicio");
  }
}
