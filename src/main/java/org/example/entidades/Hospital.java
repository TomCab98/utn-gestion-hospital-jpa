package org.example.entidades;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.entidades.enums.EspecialidadMedico;

@Entity
@Table(name = "hospitales")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(exclude = {"departamentos", "pacientes"})
public class Hospital {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String nombre;

  @Column(nullable = false, length = 300)
  private String direccion;

  @Column(length = 20)
  private String telefono;

  @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Departamento> departamentos;

  @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Paciente> pacientes;

  public static HospitalBuilder builder() {
    return new HospitalBuilder();
  }

  private Hospital(HospitalBuilder builder) {
    this.nombre = builder.nombre;
    this.direccion = builder.direccion;
    this.telefono = builder.telefono;
    this.departamentos = new ArrayList<>();
    this.pacientes = new ArrayList<>();

    validarDatosBasicos();
  }

  private void validarDatosBasicos() {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre del hospital es obligatorio");
    }
    if (direccion == null || direccion.isBlank()) {
      throw new IllegalArgumentException("La direccion del hospital es obligatoria");
    }
  }

  public void agregarDepartamento(Departamento departamento) {
    if (departamento == null) {
      throw new IllegalArgumentException("El departamento no puede ser nulo");
    }

    if (departamento.getHospital() != null && departamento.getHospital() != this) {
      throw new IllegalArgumentException(
          "El departamento ya pertenece a un hospital"
      );
    }

    if (!this.departamentos.contains(departamento)) {
      this.departamentos.add(departamento);
      departamento.setHospital(this);
    }
  }

  public void eliminarDepartamento(Departamento departamento) {
    if (departamento != null && this.departamentos.remove(departamento)) {
      if (departamento.getHospital() == this) {
        departamento.setHospital(null);
      }
    }
  }

  public void agregarPaciente(Paciente paciente) {
    if (paciente == null) {
      throw new IllegalArgumentException("El paciente no puede ser nulo");
    }

    if (paciente.getHospital() != null && paciente.getHospital() != this) {
      throw new IllegalArgumentException(
          "El paciente ya esta registrado en un hospital"
      );
    }

    if (!this.pacientes.contains(paciente)) {
      this.pacientes.add(paciente);
      paciente.setHospital(this);
    }
  }

  public void eliminarPaciente(Paciente paciente) {
    if (paciente != null && this.pacientes.remove(paciente)) {
      if (paciente.getHospital() == this) {
        paciente.setHospital(null);
      }
    }
  }

  public List<Departamento> getDepartamentos() {
    return Collections.unmodifiableList(departamentos);
  }

  public List<Paciente> getPacientes() {
    return Collections.unmodifiableList(pacientes);
  }

  List<Departamento> getInternalDepartamentos() {
    return departamentos;
  }

  List<Paciente> getInternalPacientes() {
    return pacientes;
  }

  public Departamento buscarPorEspecialidad(EspecialidadMedico especialidad) {
    return departamentos.stream()
        .filter(d -> d.getEspecialidad() == especialidad)
        .findFirst()
        .orElse(null);
  }

  public int cantidadDepartamentos() {
    return departamentos.size();
  }

  public int cantidadPacientes() {
    return pacientes.size();
  }

  public boolean tieneDepartamentos() {
    return !departamentos.isEmpty();
  }

  public boolean tienePacientes() {
    return !pacientes.isEmpty();
  }

  // Reemplazar o agregar en `Hospital.java`
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Hospital {");
    sb.append("nombre: ").append(nombre != null ? nombre : "n/a");
    sb.append(", direccion: ").append(direccion != null ? direccion : "n/a");
    sb.append(", telefono: ").append(telefono != null ? telefono : "n/a");
    sb.append(", departamentos: ");
    if (departamentos == null || departamentos.isEmpty()) {
      sb.append("[]");
    } else {
      sb.append("[");
      for (int i = 0; i < departamentos.size(); i++) {
        Departamento d = departamentos.get(i);
        sb.append(d != null ? d.getNombre() : "null");
        if (i < departamentos.size() - 1) sb.append(", ");
      }
      sb.append("]");
    }
    sb.append(", pacientes: ").append(pacientes != null ? pacientes.size() : 0);
    sb.append("}");
    return sb.toString();
  }


  public static class HospitalBuilder {
    private String nombre;
    private String direccion;
    private String telefono;

    public HospitalBuilder nombre(String nombre) {
      this.nombre = nombre;
      return this;
    }

    public HospitalBuilder direccion(String direccion) {
      this.direccion = direccion;
      return this;
    }

    public HospitalBuilder telefono(String telefono) {
      this.telefono = telefono;
      return this;
    }

    public Hospital build() {
      return new Hospital(this);
    }
  }
}
