package org.example.repositorios;

import jakarta.persistence.EntityManager;

public class RepositoryManager {
  public static <T> T mergeEntity(EntityManager em, T entity) {
    return em.merge(entity);
  }
}
