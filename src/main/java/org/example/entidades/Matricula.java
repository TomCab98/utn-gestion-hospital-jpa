package org.example.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
public class Matricula implements Serializable {

  private static final Pattern FORMATO = Pattern.compile("^MP-\\d{5}$");

  @Column(name = "matricula", unique = true, nullable = false, length = 10)
  private String matricula;

  private Matricula(String matricula) {
    Objects.requireNonNull(matricula, "La matricula no puede ser nula");

    if (!esValida(matricula)) {
      throw new IllegalArgumentException(
          "Formato de matricula invalido. Debe ser MP-XXXXX (5 digitos). Ej: MP-12345"
      );
    }

    this.matricula = matricula;
  }

  protected Matricula() {}

  public static Matricula of(String matricula) {
    return new Matricula(matricula);
  }

  public boolean esValida(String matricula) {
    return FORMATO.matcher(matricula).matches();
  }

  @Override
  public String toString() {
    return "Matricula{" + "numero=" + (matricula != null ? matricula : "n/a") + "}";
  }

  public String getNumeroSafe() {
    return matricula != null ? matricula : "n/a";
  }

}