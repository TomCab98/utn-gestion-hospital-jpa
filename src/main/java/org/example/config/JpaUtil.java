package org.example.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.function.Consumer;
import java.util.function.Function;

public final class JpaUtil {
  private static final EntityManagerFactory EMF =
      Persistence.createEntityManagerFactory("hospital-persistence-unit");

  private JpaUtil() {}

  public static EntityManager createEntityManager() {
    return EMF.createEntityManager();
  }

  // Para operaciones que retornan un valor (ej. buscar, guardar y devolver)
  public static <T> T executeInTransaction(Function<EntityManager, T> action) {
    try (EntityManager em = createEntityManager()) {
      EntityTransaction tx = em.getTransaction();
      try {
        tx.begin();
        T result = action.apply(em);
        tx.commit();
        return result;
      } catch (RuntimeException ex) {
        if (tx.isActive()) {
          tx.rollback();
        }
        throw ex;
      }
    } // El try-with-resources cierra automáticamente el EntityManager
  }

  // Para operaciones void (ej. borrar, actualizar sin retorno)
  public static void executeInTransactionVoid(Consumer<EntityManager> action) {
    executeInTransaction(em -> {
      action.accept(em);
      return null;
    });
  }

  // Para operaciones de solo lectura
  public static <T> T executeReadOnly(Function<EntityManager, T> action) {
    try (EntityManager em = createEntityManager()) {
      return action.apply(em);
    }
  }
}
