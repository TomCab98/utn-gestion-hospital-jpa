package org.example.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.Optional;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;

public class HospitalRepository extends GenericRepository<Hospital, Long> {

  private static HospitalRepository instance;

  private HospitalRepository() {
    super(Hospital.class);
  }

  public static HospitalRepository getInstance() {
    if (instance == null) {
      instance = new HospitalRepository();
    }
    return instance;
  }

  public Optional<Hospital> findByIdWithDepartamentos(EntityManager em, Long id) {
    try {
      Hospital hospital = em.createQuery(
              "SELECT h FROM Hospital h LEFT JOIN FETCH h.departamentos WHERE h.id = :id",
              Hospital.class)
          .setParameter("id", id)
          .getResultStream()
          .findFirst()
          .orElse(null);
      return Optional.ofNullable(hospital);
    } catch (Exception e) {
      throw new PersistenceException("No se pudo buscar el hospital con departamentos", e);
    }
  }

  public Optional<Hospital> findByIdWithDepartamentosDetalles(EntityManager em, Long id) {
    try {
      Hospital hospital = em.createQuery(
              "SELECT h FROM Hospital h LEFT JOIN FETCH h.departamentos WHERE h.id = :id",
              Hospital.class)
          .setParameter("id", id)
          .getResultStream()
          .findFirst()
          .orElse(null);

      if (hospital != null) {
        for (Departamento departamento : hospital.getDepartamentos()) {
          departamento.getMedicos().size();
          departamento.getSalas().size();
        }
      }

      return Optional.ofNullable(hospital);
    } catch (Exception e) {
      throw new PersistenceException("No se pudo buscar el hospital con detalles", e);
    }
  }
}
