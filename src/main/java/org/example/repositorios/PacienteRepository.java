package org.example.repositorios;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.example.entidades.Paciente;

public class PacienteRepository extends GenericRepository<Paciente, Long> {

  private static PacienteRepository instance;

  private PacienteRepository() {
    super(Paciente.class);
  }

  public static PacienteRepository getInstance() {
    if (instance == null) {
      instance = new PacienteRepository();
    }
    return instance;
  }

  public List<Paciente> obtenerPorHospital(EntityManager em, Long hospitalId) {
    return em.createQuery(
            "SELECT h FROM Paciente h WHERE h.hospital.id = :hospitalId",
            Paciente.class)
        .setParameter("hospitalId", hospitalId)
        .getResultStream().toList();
  }
}
