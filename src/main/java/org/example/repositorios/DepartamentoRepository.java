package org.example.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.List;
import org.example.entidades.Departamento;

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

  public List<Departamento> findByHospital(EntityManager em, Long hospitalId) {
    try {
      return em.createQuery(
              "SELECT h FROM Departamento h WHERE h.hospital.id = :hospitalId",
              Departamento.class)
          .setParameter("hospitalId", hospitalId)
          .getResultStream().toList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudo buscar los departamentos", e);
    }
  }

  public List<Departamento> findByHospitalWithSalas(EntityManager em, Long hospitalId) {
    try {
      return em.createQuery(
              "SELECT DISTINCT d FROM Departamento d " +
                  "LEFT JOIN FETCH d.salas " +
                  "WHERE d.hospital.id = :hospitalId",
              Departamento.class)
          .setParameter("hospitalId", hospitalId)
          .getResultStream()
          .toList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudo buscar los departamentos con salas", e);
    }
  }
}
