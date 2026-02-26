package org.example.ui;

import static org.example.Main.SCANNER;

import java.util.List;
import org.example.entidades.Sala;
import org.example.entidades.enums.TipoSala;
import org.example.servicio.SalaService;

public class SalaUI {

  private static SalaUI instance;

  private SalaUI() {}

  public static SalaUI getInstance() {
    if (instance == null) {
      instance = new SalaUI();
    }
    return instance;
  }

  private final SalaService salaService = SalaService.getInstance();

  public Sala crearSala() {
    System.out.println("\n=== Formulario de creacion de sala ===");
    System.out.print("Numero de sala: ");
    String numero = SCANNER.nextLine().trim();

    TipoSala tipoSala = null;
    boolean tipoSeleccionado = false;
    while (!tipoSeleccionado) {
      System.out.println("Seleccione tipo de sala: ");
      for (int i = 0; i < TipoSala.values().length; i++) {
        System.out.println((i + 1) + ". " + TipoSala.values()[i].name());
      }
      System.out.print("numero > ");
      String esp = SCANNER.nextLine().trim();
      try {
        tipoSala = TipoSala.values()[Integer.parseInt(esp) - 1];
        tipoSeleccionado = true;
      } catch (Exception e) {
        System.out.println("Tipo no valido, intente nuevamente.");
      }
    }

    return Sala.builder()
        .numero(numero)
        .tipo(tipoSala)
        .build();
  }

  public void verSalas(Long departamentoId) {
    List<Sala> salas = salaService.obtenerTodos(departamentoId);
    if (salas.isEmpty()) {
      System.out.println("No hay salas registrados para este departamento.");
      return;
    }

    System.out.println("\n=== Lista de Salas ===\n");
    salas.forEach(sala -> {
      System.out.println("Numero: " + sala.getNumero());
      System.out.println("Especialidad: " + sala.getTipo());
      System.out.println("-------------------------");
    });
  }
}
