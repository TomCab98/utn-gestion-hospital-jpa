package org.example.repositorios;

import jakarta.persistence.EntityManager;

public class RepositoryManager {
  public static <T> T mergeEntity(EntityManager em, T entity) {
    try {
      em.getTransaction().begin();
      T merged = em.merge(entity);
      em.getTransaction().commit();
      return merged;
    } catch (Exception ex) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw ex;
    }
  }
}
