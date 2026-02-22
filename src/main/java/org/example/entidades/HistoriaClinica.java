package org.example.entidades;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "historias_clinicas")
@Getter
@NoArgsConstructor
public class HistoriaClinica {

  @Id
  @Column(name = "numero", nullable = false, unique = true, length = 100)
  private String numero;

  @OneToOne(optional = false)
  @JoinColumn(name = "paciente_id", unique = true, nullable = false)
  private Paciente paciente;

  @ElementCollection
  @CollectionTable(
      name = "diagnosticos",
      joinColumns = @JoinColumn(name = "numero_historia")
  )
  @Column(name = "diagnostico", nullable = false)
  private List<String> diagnosticos = new ArrayList<>();

  @ElementCollection
  @CollectionTable(
      name = "tratamientos",
      joinColumns = @JoinColumn(name = "numero_historia")
  )
  @Column(name = "tratamiento", nullable = false)
  private List<String> tratamientos = new ArrayList<>();

  @ElementCollection
  @CollectionTable(
      name = "alergias",
      joinColumns = @JoinColumn(name = "numero_historia")
  )
  @Column(name = "alergia", nullable = false)
  private List<String> alergias = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "fecha_creacion", updatable = false, nullable = false)
  private Instant fecha;

  private HistoriaClinica(HistoriaClinicaBuilder builder) {
    this.numero = generarNumeroHistoria(builder.paciente);
    this.paciente = builder.paciente;
    this.fecha = Instant.now();
  }

  public static HistoriaClinicaBuilder builder() {
    return new HistoriaClinicaBuilder();
  }

  private static String generarNumeroHistoria(Paciente paciente) {
    String dni = paciente.getDni();
    String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        .replace(":", "")
        .replace("-", "")
        .replace("Z", "");
    return "HC-" + dni + "-" + timestamp;
  }

  void agregarTratamiento(String tratamiento) {
    this.tratamientos.add(tratamiento);
  }

  void agregarAlergia(String alergias) {
    this.alergias.add(alergias);
  }

  void agregarDiagnostico(String diagnosticos) {
    this.diagnosticos.add(diagnosticos);
  }

  String getNumeroHistoria() {
    return numero;
  }

  // Reemplazar o agregar en `HistoriaClinica.java`
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("HistoriaClinica{");
    sb.append("diagnosticos=").append(diagnosticos != null ? diagnosticos : "[]");
    sb.append(", tratamientos=").append(tratamientos != null ? tratamientos : "[]");
    sb.append(", alergias=").append(alergias != null ? alergias : "[]");
    sb.append("}");
    return sb.toString();
  }


  public static class HistoriaClinicaBuilder {
    private Paciente paciente;

    public HistoriaClinicaBuilder paciente(Paciente paciente) {
      this.paciente = paciente;
      return this;
    }

    public HistoriaClinica build() {
      return new HistoriaClinica(this);
    }
  }
}
