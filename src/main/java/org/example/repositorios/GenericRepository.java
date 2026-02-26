package org.example.repositorios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Clase genérica CRUD para operaciones de persistencia con JPA.
 * Proporciona métodos estándar para crear, leer, actualizar y eliminar entidades.
 *
 * @param <T> el tipo de entidad a persistir
 * @param <ID> el tipo del identificador único de la entidad
 */
public class GenericRepository<T, ID> {

  private final Class<T> entityClass;

  /**
   * Constructor que inicializa el repositorio con la fábrica de EntityManager
   * y la clase de la entidad.
   *
   * @param entityClass la clase de la entidad a gestionar
   * @throws IllegalArgumentException si emf o entityClass es null
   */
  public GenericRepository(Class<T> entityClass) {
    this.entityClass = Objects.requireNonNull(entityClass, "entityClass no puede ser null");
  }

  /**
   * Crea y persiste una nueva entidad en la base de datos.
   *
   * @param entity la entidad a crear
   * @return la entidad persistida
   * @throws IllegalArgumentException si entity es null
   * @throws PersistenceException si ocurre un error durante la persistencia
   */
  public T create(EntityManager em, T entity) {
    Objects.requireNonNull(entity, "La entidad no puede ser null");
    try {
      em.persist(entity);
      return entity;
    } catch (Exception e) {
      throw new PersistenceException("No se pudo crear la entidad", e);
    }
  }

  /**
   * Busca una entidad por su identificador único.
   *
   * @param id el identificador de la entidad
   * @return un Optional que contiene la entidad si existe, vacío en caso contrario
   * @throws IllegalArgumentException si id es null
   */
  public Optional<T> findById(EntityManager em, ID id) {
    Objects.requireNonNull(id, "El ID no puede ser null");
    try {
      T entity = em.find(entityClass, id);
      return Optional.ofNullable(entity);
    } catch (Exception e) {
      throw new PersistenceException("No se pudo buscar la entidad por ID", e);
    }
  }

  /**
   * Obtiene todas las entidades del tipo especificado.
   *
   * @return una lista de todas las entidades, o lista vacía si no hay resultados
   */
  public List<T> findAll(EntityManager em) {
    try {
      String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
      return em.createQuery(jpql, entityClass).getResultList();
    } catch (Exception e) {
      throw new PersistenceException("No se pudieron obtener las entidades", e);
    }
  }

  /**
   * Actualiza una entidad existente.
   *
   * @param entity la entidad con los datos actualizados
   * @return la entidad actualizada
   * @throws IllegalArgumentException si entity es null
   * @throws PersistenceException si ocurre un error durante la actualización
   */
  public T update(EntityManager em, T entity) {
    Objects.requireNonNull(entity, "La entidad no puede ser null");
    try {
      T merged = em.merge(entity);
      return merged;
    } catch (Exception e) {
      throw new PersistenceException("No se pudo actualizar la entidad", e);
    }
  }

  /**
   * Elimina una entidad existente por su identificador.
   *
   * @param id el identificador de la entidad a eliminar
   * @return true si la entidad fue eliminada, false si no existía
   * @throws IllegalArgumentException si id es null
   * @throws PersistenceException si ocurre un error durante la eliminación
   */
  public boolean deleteById(EntityManager em, ID id) {
    Objects.requireNonNull(id, "El ID no puede ser null");
    try {
      T entity = em.find(entityClass, id);
      if (entity == null) {
        return false;
      }
      em.remove(entity);
      return true;
    } catch (Exception e) {
      throw new PersistenceException("No se pudo eliminar la entidad", e);
    }
  }

  /**
   * Elimina una entidad existente.
   *
   * @param entity la entidad a eliminar
   * @return true si la entidad fue eliminada, false en caso contrario
   * @throws IllegalArgumentException si entity es null
   * @throws PersistenceException si ocurre un error durante la eliminación
   */
  public boolean delete(EntityManager em, T entity) {
    Objects.requireNonNull(entity, "La entidad no puede ser null");
    try {
      T managed = em.merge(entity);
      em.remove(managed);
      return true;
    } catch (Exception e) {
      throw new PersistenceException("No se pudo eliminar la entidad", e);
    }
  }

  /**
   * Cuenta el número total de entidades del tipo especificado.
   *
   * @return la cantidad de entidades
   */
  public long count(EntityManager em) {
    try {
      String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
      return em.createQuery(jpql, Long.class).getSingleResult();
    } catch (Exception e) {
      throw new PersistenceException("No se pudo contar las entidades", e);
    }
  }

  /**
   * Verifica si existe una entidad con el identificador especificado.
   *
   * @param id el identificador a verificar
   * @return true si la entidad existe, false en caso contrario
   * @throws IllegalArgumentException si id es null
   */
  public boolean existsById(EntityManager em, ID id) {
    Objects.requireNonNull(id, "El ID no puede ser null");
    return findById(em, id).isPresent();
  }
}
