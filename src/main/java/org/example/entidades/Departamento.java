package org.example.entidades;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entidades.enums.EspecialidadMedico;
import org.example.entidades.enums.TipoSala;

@Entity
@Table(name = "departamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Departamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "nombre", nullable = false)
  private String nombre;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private EspecialidadMedico especialidad;

  @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Medico> medicos;

  @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Sala> salas;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "hospital_id", nullable = false)
  private Hospital hospital;

  public static DepartamentoBuilder builder() {
    return new DepartamentoBuilder();
  }

  private Departamento(DepartamentoBuilder builder) {
    this.especialidad =  builder.especialidad;
    this.hospital = builder.hospital;
    this.nombre = builder.nombre;
    this.medicos = builder.medicos != null ? new ArrayList<>(builder.medicos) : new ArrayList<>();
    this.salas = builder.salas  != null ? new ArrayList<>(builder.salas) : new ArrayList<>();
  }

  public Sala crearSala(Integer numero, TipoSala tipo) {
    if (numero == null) {
      throw new IllegalArgumentException("El numero de sala es obligatorio");
    }
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de sala es obligatorio");
    }

    Sala sala = Sala.builder()
        .tipo(tipo)
        .departamento(this)
        .build();

    this.agregarSala(sala);
    return sala;
  }

  public void agregarMedico(Medico medico) {
    if (medico == null) throw new IllegalArgumentException("El medico no puede ser nulo");
    if (!this.especialidad.equals(medico.getEspecialidad()))
      throw new IllegalArgumentException("Especialidad del medico incompatible con el departamento");

    if (medico.getDepartamento() != null && medico.getDepartamento() != this)
      throw new IllegalArgumentException("El medico ya pertenece a otro departamento");

    if (!this.medicos.contains(medico)) {
      this.medicos.add(medico);
      medico.setDepartamento(this);
    }
  }

  public void eliminarMedico(Medico medico) {
    if (medico != null && this.medicos.remove(medico)) {
      if (medico.getDepartamento() == this) {
        medico.setDepartamento(null);
      }
    }
  }

  public void agregarSala(Sala sala) {
    if (sala == null) throw new IllegalArgumentException("La sala no puede ser nula");
    if (sala.getDepartamento() != null && sala.getDepartamento() != this)
      throw new IllegalArgumentException("La sala ya pertenece a otro departamento");

    if (!this.salas.contains(sala)) {
      this.salas.add(sala);
      sala.setDepartamento(this);
    }
  }

  public void eliminarSala(Sala sala) {
    if (sala != null && this.salas.remove(sala)) {
      if (sala.getDepartamento() == this) {
        sala.setDepartamento(null);
      }
    }
  }

  public List<Medico> getMedicos() {
    return Collections.unmodifiableList(medicos);
  }

  public List<Sala> getSalas() {
    return Collections.unmodifiableList(salas);
  }

  public Sala buscarSalaPorNumero(String numero) {
    return salas.stream()
        .filter(s -> s.getNumero().equals(numero))
        .findFirst()
        .orElse(null);
  }

  public boolean tieneMedicos() {
    return !medicos.isEmpty();
  }

  public boolean tieneSalas() {
    return !salas.isEmpty();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Departamento that)) return false;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @Override
  public String toString() {
    return "Departamento { "
        + "nombre: " + (nombre != null ? nombre : "n/a")
        + ", especialidad: " + (especialidad != null ? especialidad : "n/a")
        + " }";
  }


  public static class DepartamentoBuilder {
    private String nombre;
    private EspecialidadMedico especialidad;
    private List<Sala> salas;
    private Hospital hospital;
    private List<Medico> medicos;

    public DepartamentoBuilder nombre(String nombre) {
      this.nombre = nombre;
      return this;
    }

    public DepartamentoBuilder especialidad(EspecialidadMedico especialidad) {
      this.especialidad = especialidad;
      return this;
    }

    public DepartamentoBuilder salas(List<Sala> salas) {
      this.salas = salas;
      return this;
    }

    public DepartamentoBuilder hospital(Hospital hospital) {
      this.hospital = hospital;
      return this;
    }

    public DepartamentoBuilder medicos(List<Medico> medicos) {
      this.medicos = medicos;
      return this;
    }

    public Departamento build() {
      return new Departamento(this);
    }
  }
}
