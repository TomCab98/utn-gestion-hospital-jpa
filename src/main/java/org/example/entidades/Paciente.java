package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pacientes")
@Getter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"hospital", "historiaClinica", "citas"})
public class Paciente extends Persona {

  @Column(length = 20)
  private String telefono;

  @Column(length = 200)
  private String direccion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hospital_id")
  private Hospital hospital;

  @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
  private HistoriaClinica historiaClinica;

  @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Cita> citas;

  private Paciente(PacienteBuilder<?, ?> builder) {
    super(builder);
    this.citas = new ArrayList<>();

    this.historiaClinica = HistoriaClinica.builder()
        .paciente(this)
        .build();
  }

  public void agregarCita(Cita cita) {
    if (cita == null) {
      throw new IllegalArgumentException("La cita no puede ser nula");
    }

    if (!this.citas.contains(cita)) {
      this.citas.add(cita);
      if (cita.getPaciente() != this) {
        cita.setPaciente(this);
      }
    }
  }

  public void eliminarCita(Cita cita) {
    if (cita != null && this.citas.remove(cita)) {
      if (cita.getPaciente() == this) {
        cita.setPaciente(null);
      }
    }
  }

  public List<Cita> getCitas() {
    return Collections.unmodifiableList(citas);
  }

  List<Cita> getInternalCitas() {
    return citas;
  }

  public boolean tieneCitasProgramadas() {
    return citas != null && !citas.isEmpty();
  }

  public int cantidadCitas() {
    return citas != null ? citas.size() : 0;
  }

  public boolean tieneAlergias() {
    return historiaClinica != null && !historiaClinica.getAlergias().isEmpty();
  }

  void setHospital(Hospital hospital) {
    this.hospital = hospital;
  }

  public List<String> getAlergias() {
    return historiaClinica != null
        ? historiaClinica.getAlergias()
        : Collections.emptyList();
  }

  public void agregarDiagnostico(String diagnostico) {
    historiaClinica.agregarDiagnostico(diagnostico);
  }

  public void agregarTratamiento(String tratamiento) {
    historiaClinica.agregarTratamiento(tratamiento);
  }

  public void agregarAlergia(String alergia) {
    historiaClinica.agregarAlergia(alergia);
  }

  public String getNumeroHistoriaClinica() {
    return historiaClinica != null ? historiaClinica.getNumeroHistoria() : null;
  }

  public boolean tieneDatosContacto() {
    return (telefono != null && !telefono.isBlank())
        || (direccion != null && !direccion.isBlank());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Paciente{");
    sb.append("id=").append(this.getIdSafe());
    sb.append(", nombre=").append(nombre != null ? nombre + " " + (apellido != null ? apellido : "") : "n/a");
    sb.append(", dni=").append(dni != null ? dni : "n/a");
    sb.append(", fechaNacimiento=").append(fechaNacimiento != null ? fechaNacimiento : "n/a");
    sb.append(", tipoSangre=").append(tipoSangre != null ? tipoSangre : "n/a");
    sb.append(", telefono=").append(telefono != null ? telefono : "n/a");
    sb.append(", direccion=").append(direccion != null ? direccion : "n/a");
    if (historiaClinica != null) {
      sb.append(", historiaClinica={diagnosticos=").append(historiaClinica.getDiagnosticos().size())
          .append(", tratamientos=").append(historiaClinica.getTratamientos().size())
          .append(", alergias=").append(historiaClinica.getAlergias().size()).append("}");
    } else {
      sb.append(", historiaClinica=n/a");
    }
    sb.append("}");
    return sb.toString();
  }

  private Object getIdSafe() {
    try {
      return this.getClass().getMethod("getId").invoke(this);
    } catch (Exception e) {
      return "n/a";
    }
  }

}
