package org.example.servicio.comandos;

import static org.example.Main.SCANNER;

import org.example.entidades.Departamento;
import org.example.entidades.Sala;
import org.example.entidades.enums.TipoSala;

public class ComandosSala {

  private static ComandosSala instance;

  private ComandosSala() {}

  public static ComandosSala getInstance() {
    if (instance == null) {
      instance = new ComandosSala();
    }
    return instance;
  }

  public Sala crearSala() {
    System.out.println("\n=== Formulario de creacion de sala ===");
    System.out.print("Número de sala: ");
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
}
