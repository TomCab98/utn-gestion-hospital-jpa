package org.example.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entidades.enums.EstadoCita;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "paciente_id", nullable = false)
  private Paciente paciente;

  @Setter
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "medico_id", nullable = false)
  private Medico medico;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "sala_id", nullable = false)
  private Sala sala;

  @Column(name = "fecha_hora", nullable = false)
  private LocalDateTime fechaHora;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false, length = 20)
  private EstadoCita estado;

  @Column(name = "costo_consulta", nullable = false, precision = 15, scale = 2)
  private BigDecimal costo;

  @Column(name = "observaciones", columnDefinition = "TEXT")
  private String observaciones;

  public static CitaBuilder builder() {
    return new CitaBuilder();
  }

  private Cita(CitaBuilder builder) {
    this.paciente = builder.paciente;
    this.medico = builder.medico;
    this.sala = builder.sala;
    this.fechaHora =  builder.fechaHora;
    this.estado = builder.estado;
    this.costo = builder.costo;
    this.observaciones = builder.observaciones;

    validarInvariantes();
  }

  private void validarInvariantes() {
    validarFecha();
    validarCosto();
  }

  private void validarFecha() {
    if (fechaHora == null || !fechaHora.isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("La cita debe programarse en una fecha y hora futura.");
    }
  }

  private void validarCosto() {
    if (costo == null || costo.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El costo de la consulta debe ser mayor a cero.");
    }
  }

  public void marcarComoCompletada() {
    if (this.estado == EstadoCita.CANCELADA) {
      throw new IllegalStateException("No se puede completar una cita cancelada.");
    }
    this.estado = EstadoCita.COMPLETADA;
  }

  public void cancelar() {
    if (this.estado == EstadoCita.COMPLETADA) {
      throw new IllegalStateException("No se puede cancelar una cita completada.");
    }
    this.estado = EstadoCita.CANCELADA;
  }

  public boolean estaProgramada() {
    return this.estado == EstadoCita.PROGRAMADA;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Cita cita)) return false;
    return Objects.equals(id, cita.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Cita{");
    sb.append("id=").append(id != null ? id : "n/a");
    sb.append(", paciente=").append(paciente != null ? paciente.getNombre() + " " + (paciente.getApellido() != null ? paciente.getApellido() : "") : "n/a");
    sb.append(", medico=").append(medico != null ? medico.getNombre() + " " + (medico.getApellido() != null ? medico.getApellido() : "") : "n/a");
    sb.append(", sala=").append(sala != null ? sala.getNumero() : "n/a");
    sb.append(", fecha=").append(fechaHora != null ? fechaHora : "n/a");
    sb.append(", estado=").append(estado != null ? estado : "n/a");
    sb.append(", valor=").append(costo != null ? costo : "n/a");
    sb.append("}");
    return sb.toString();
  }


  public static class CitaBuilder {
    private Paciente paciente;
    private Medico medico;
    private Sala sala;
    private BigDecimal costo;
    private String observaciones;
    private EstadoCita estado;
    private LocalDateTime fechaHora;

    public CitaBuilder paciente(Paciente paciente) {
      this.paciente = paciente;
      return this;
    }

    public CitaBuilder medico(Medico medico) {
      this.medico = medico;
      return this;
    }

    public CitaBuilder sala(Sala sala) {
      this.sala = sala;
      return this;
    }

    public CitaBuilder costo(BigDecimal costo) {
      this.costo = costo;
      return this;
    }

    public CitaBuilder observaciones(String observaciones) {
      this.observaciones = observaciones;
      return this;
    }

    public CitaBuilder estado(EstadoCita estado) {
      this.estado = estado;
      return this;
    }

    public CitaBuilder fechaHora(LocalDateTime fechaHora) {
      this.fechaHora = fechaHora;
      return this;
    }

    public Cita build() {
      return new Cita(this);
    }
  }
}
