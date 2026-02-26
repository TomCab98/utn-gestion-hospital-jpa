package org.example.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.List;
import org.example.entidades.Departamento;
import org.example.entidades.Sala;

public class SalaRepository extends GenericRepository<Sala, Long> {

  private static SalaRepository instance;

  private SalaRepository() {
    super(Sala.class);
  }

  public static SalaRepository getInstance() {
    if (instance == null) {
      instance = new SalaRepository();
    }
    return instance;
  }

  public List<Sala> findByDepartamento(EntityManager em, Long departamentoId) {
    try {
      return em.createQuery(
              "SELECT h FROM Sala h WHERE h.departamento.id = :departamentoId",
              Sala.class)
          .setParameter("departamentoId", departamentoId)
          .getResultStream().toList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudo buscar las salas", e);
    }
  }
}
