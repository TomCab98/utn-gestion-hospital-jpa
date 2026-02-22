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
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entidades.enums.TipoSala;

@Entity
@Table(name = "salas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

  @Id
  private String numero;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false)
  private TipoSala tipo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "departamento_id", nullable = false)
  private Departamento departamento;

  @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Cita> citas = new ArrayList<>();

  public static SalaBuilder builder() {
    return new SalaBuilder();
  }

  private Sala(SalaBuilder builder) {
    this.numero = builder.numero;
    this.departamento = builder.departamento;
    this.tipo = builder.tipo;
    this.citas = builder.citas != null ? new ArrayList<>(builder.citas) : new ArrayList<>();
  }

  public void agregarCita(Cita cita) {
    this.citas.add(cita);
    cita.setSala(this);
  }

  public void eliminarCita(Cita cita) {
    this.citas.remove(cita);
    cita.setSala(null);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Sala{");
    sb.append("numero=").append(numero != null ? numero : "n/a");
    sb.append(", tipo=").append(tipo != null ? tipo : "n/a");
    sb.append(", departamento=").append(departamento != null ? departamento.getNombre() : "n/a");
    sb.append("}");
    return sb.toString();
  }


  public static class SalaBuilder {
    private String numero;
    private TipoSala tipo;
    private Departamento departamento;
    private List<Cita> citas;

    public SalaBuilder tipo(TipoSala tipo) {
      this.tipo = tipo;
      return this;
    }

    public SalaBuilder departamento(Departamento departamento) {
      this.departamento = departamento;
      return this;
    }

    public SalaBuilder citas(List<Cita> citas) {
      this.citas = citas;
      return this;
    }

    public SalaBuilder numero(String numero) {
      this.numero = numero;
      return this;
    }

    public Sala build() {
      return new Sala(this);
    }
  }
}
