package org.example.entidades;

import jakarta.persistence.*;
import java.util.regex.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.entidades.enums.TipoSangre;

import java.time.LocalDate;
import java.time.Period;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class Persona {

  @Id
  protected String dni;

  @Column(nullable = false, length = 50)
  protected String nombre;

  @Column(nullable = false, length = 50)
  protected String apellido;

  @Column(name = "fecha_nacimiento", nullable = false)
  protected LocalDate fechaNacimiento;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_sangre", nullable = false, length = 20)
  protected TipoSangre tipoSangre;

  @Transient
  private static final Pattern DNI_PATTERN = Pattern.compile("^\\d{7,8}$");

  @PrePersist
  @PreUpdate
  private void validarDni() {
    if (dni == null || !DNI_PATTERN.matcher(dni).matches()) {
      throw new IllegalArgumentException("DNI invalido. Debe tener 7 u 8 digitos numericos.");
    }
  }

  public int calcularEdad() {
    return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
  }

  public String getNombreCompleto() {
    return apellido + ", " + nombre;
  }
}
