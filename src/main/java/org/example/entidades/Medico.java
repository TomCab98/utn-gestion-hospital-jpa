package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.example.entidades.enums.EspecialidadMedico;

@Entity
@Table(name = "medicos")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Medico extends Persona {

  @Embedded
  @AttributeOverride(name = "numero", column = @Column(name = "matricula", nullable = false, unique = true))
  private Matricula matricula;

  @Enumerated(EnumType.STRING)
  @Column(name = "especialidad", nullable = false, length = 50)
  private EspecialidadMedico especialidad;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "departamento_id", nullable = false)
  private Departamento departamento;

  @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Cita> citas;

  protected Medico(MedicoBuilder<?, ?> builder) {
    super(builder);
    this.matricula = builder.matricula;
    this.especialidad = builder.especialidad;
    this.departamento = builder.departamento;

    this.citas = new ArrayList<>();
  }

  public void agregarCita(Cita cita) {
    Objects.requireNonNull(cita, "Cita no puede ser nula");
    if (!this.citas.contains(cita)) {
      this.citas.add(cita);
      cita.setMedico(this);
    }
  }

  public void eliminarCita(Cita cita) {
    if (cita != null && this.citas.remove(cita)) {
      cita.setMedico(null);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Medico medico)) return false;
    return matricula != null && matricula.equals(medico.matricula);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(matricula);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Medico{");
    sb.append("matricula=").append(matricula != null ? matricula.getMatricula() : "n/a");
    sb.append(", especialidad=").append(especialidad != null ? especialidad : "n/a");
    sb.append(", departamento=").append(departamento != null ? departamento.getNombre() : "n/a");
    sb.append(", citas=");
    if (citas == null || citas.isEmpty()) {
      sb.append("[]");
    } else {
      sb.append("[");
      for (int i = 0; i < citas.size(); i++) {
        Cita c = citas.get(i);
        sb.append(c != null ? String.valueOf(c.getId()) : "null");
        if (i < citas.size() - 1) sb.append(", ");
      }
      sb.append("]");
    }
    sb.append("}");
    return sb.toString();
  }

}

