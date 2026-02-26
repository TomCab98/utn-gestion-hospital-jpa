package org.example.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Sala;

public class CitaRepository extends GenericRepository<Cita, Long> {

  private static CitaRepository instance;

  private CitaRepository() {
    super(Cita.class);
  }

  public static CitaRepository getInstance() {
    if (instance == null) {
      instance = new CitaRepository();
    }
    return instance;
  }

  public List<Cita> findByMedicoAndFechaBetween(
      EntityManager em,
      Medico medico,
      LocalDateTime desde,
      LocalDateTime hasta
  ) {
    try {
      return em.createQuery(
              "SELECT c FROM Cita c WHERE c.medico = :medico AND c.fechaHora BETWEEN :desde AND :hasta",
              Cita.class)
          .setParameter("medico", medico)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultStream()
          .toList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudieron buscar citas del medico", e);
    }
  }

  public List<Cita> findBySalaAndFechaBetween(
      EntityManager em,
      Sala sala,
      LocalDateTime desde,
      LocalDateTime hasta
  ) {
    try {
      return em.createQuery(
              "SELECT c FROM Cita c WHERE c.sala = :sala AND c.fechaHora BETWEEN :desde AND :hasta",
              Cita.class)
          .setParameter("sala", sala)
          .setParameter("desde", desde)
          .setParameter("hasta", hasta)
          .getResultStream()
          .toList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudieron buscar citas de la sala", e);
    }
  }
}
