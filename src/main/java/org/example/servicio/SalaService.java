package org.example.servicio;

import java.util.List;
import org.example.config.JpaUtil;
import org.example.entidades.Departamento;
import org.example.entidades.Sala;
import org.example.repositorios.SalaRepository;

public class SalaService {

  private static SalaService instance;

  private SalaService() {}

  public static SalaService getInstance() {
    if (instance == null) {
      instance = new SalaService();
    }
    return instance;
  }

  private final SalaRepository salaRepository = SalaRepository.getInstance();

  public List<Sala> obtenerTodos(Long departamentoId) {
    return JpaUtil.executeReadOnly(em -> salaRepository.findByDepartamento(em, departamentoId));
  }
}
