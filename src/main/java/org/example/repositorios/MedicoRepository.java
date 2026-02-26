package org.example.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.List;
import org.example.entidades.Medico;

public class MedicoRepository extends GenericRepository<Medico, Long> {

  private static MedicoRepository instance;

  private MedicoRepository() {
    super(Medico.class);
  }

  public static MedicoRepository getInstance() {
    if (instance == null) {
      instance = new MedicoRepository();
    }
    return instance;
  }

  public List<Medico> findByDepartamento(EntityManager em, Long departamentoId) {
    try {
      return em.createQuery(
              "SELECT m FROM Medico m WHERE m.departamento.id = :departamentoId",
              Medico.class)
          .setParameter("departamentoId", departamentoId)
          .getResultStream()
          .toList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudieron buscar los medicos", e);
    }
  }
}
