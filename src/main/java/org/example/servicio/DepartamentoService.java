package org.example.servicio;

import java.util.List;
import org.example.config.JpaUtil;
import org.example.entidades.Departamento;
import org.example.entidades.Sala;
import org.example.repositorios.DepartamentoRepository;

public class DepartamentoService {

  private static DepartamentoService instance;

  private DepartamentoService() {}

  public static DepartamentoService getInstance() {
    if (instance == null) {
      instance = new DepartamentoService();
    }
    return instance;
  }

  private final DepartamentoRepository departamentoRepository = DepartamentoRepository.getInstance();

  public void crearSala(Long departamentoId, Sala nuevaSala) {
    JpaUtil.executeInTransactionVoid(em -> {
      Departamento departamento = departamentoRepository.findById(em, departamentoId)
          .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));

      departamento.agregarSala(nuevaSala);
    });
  }

  public List<Departamento> obtenerTodos(Long hospitalId) {
    return JpaUtil.executeReadOnly(em -> departamentoRepository.findByHospital(em, hospitalId));
  }
}
