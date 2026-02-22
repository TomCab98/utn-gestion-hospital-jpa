package org.example.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;

public class DepartamentoRepository extends GenericRepository<Departamento, Long> {

  private static DepartamentoRepository instance;

  private DepartamentoRepository() {
    super(Departamento.class);
  }

  public static DepartamentoRepository getInstance() {
    if (instance == null) {
      instance = new DepartamentoRepository();
    }
    return instance;
  }

  public List<Departamento> findByHospital(Long hospitalId) {
    try (EntityManager em = createEntityManager()) {
      return em.createQuery(
              "SELECT h FROM Departamento h WHERE h.hospital.id = :hospitalId",
              Departamento.class)
          .setParameter("hospitalId", hospitalId)
          .getResultStream().toList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudo buscar los departamentos", e);
    }
  }
}
