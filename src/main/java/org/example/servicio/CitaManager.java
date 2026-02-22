package org.example.servicio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;
import org.example.entidades.enums.EstadoCita;
import org.example.excepciones.CitaException;

public class CitaManager implements CitaService {
  private static final Duration BUFFER = Duration.ofHours(2);


  private final EntityManagerFactory emf;

  public CitaManager() {
    this.emf = Persistence.createEntityManagerFactory("hospital-persistence-unit");
  }

  @Override
  public Cita programarCita(
      Paciente paciente,
      Medico medico,
      Sala sala,
      LocalDateTime fechaHora,
      BigDecimal costo
  ) throws CitaException {

    if (paciente == null) {
      throw new CitaException("Paciente no puede ser nulo");
    };
    if (medico == null) {
      throw new CitaException("Medico no puede ser nulo");
    }
    if (sala == null) {
      throw new CitaException("Sala no puede ser nula");
    }
    if (fechaHora == null) {
      throw new CitaException("Fecha/hora de la cita es obligatoria");
    }
    if (costo == null) {
      throw new CitaException("Costo es obligatorio");
    }

    if (!fechaHora.isAfter(LocalDateTime.now())) {
      throw new CitaException("La cita debe programarse en una fecha/hora futura");
    }
    if (costo.compareTo(BigDecimal.ZERO) <= 0) {
      throw new CitaException("El costo de la consulta debe ser mayor a cero");
    }

    if (medico.getEspecialidad() == null
        || sala.getDepartamento() == null
        || !medico.getEspecialidad().equals(sala.getDepartamento().getEspecialidad())) {
      throw new CitaException("La especialidad del medico no coincide con el departamento de la sala");
    }

    EntityManager em = emf.createEntityManager();
    try {
      em.getTransaction().begin();

      paciente = em.contains(paciente) ? paciente : em.merge(paciente);
      medico = em.contains(medico) ? medico : em.merge(medico);
      sala = em.contains(sala) ? sala : em.merge(sala);

      LocalDateTime desde = fechaHora.minus(BUFFER);
      LocalDateTime hasta = fechaHora.plus(BUFFER);

      TypedQuery<Cita> qMedico = em.createQuery(
          "SELECT c FROM Cita c WHERE c.medico = :medico AND c.fechaHora BETWEEN :desde AND :hasta",
          Cita.class
      );
      qMedico.setParameter("medico", medico);
      qMedico.setParameter("desde", desde);
      qMedico.setParameter("hasta", hasta);
      List<Cita> colisionesMedico = qMedico.getResultList();
      if (!colisionesMedico.isEmpty()) {
        em.getTransaction().rollback();
        throw new CitaException("El medico no esta disponible en ese rango horario");
      }

      TypedQuery<Cita> qSala = em.createQuery(
          "SELECT c FROM Cita c WHERE c.sala = :sala AND c.fechaHora BETWEEN :desde AND :hasta",
          Cita.class
      );
      qSala.setParameter("sala", sala);
      qSala.setParameter("desde", desde);
      qSala.setParameter("hasta", hasta);
      List<Cita> colisionesSala = qSala.getResultList();
      if (!colisionesSala.isEmpty()) {
        em.getTransaction().rollback();
        throw new CitaException("La sala no esta disponible en ese rango horario");
      }

      Cita cita = Cita.builder()
          .paciente(paciente)
          .medico(medico)
          .sala(sala)
          .estado(EstadoCita.PROGRAMADA)
          .costo(costo)
          .build();

      paciente.agregarCita(cita);
      medico.agregarCita(cita);
      sala.agregarCita(cita);

      em.persist(cita);

      em.getTransaction().commit();
      return cita;
    } catch (CitaException e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw e;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new CitaException("Error al programar la cita: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }
}
