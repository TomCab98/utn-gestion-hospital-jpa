package org.example.servicio;

import java.util.List;
import org.example.config.JpaUtil;
import org.example.entidades.Medico;
import org.example.entidades.Departamento;
import org.example.repositorios.DepartamentoRepository;
import org.example.repositorios.MedicoRepository;

public class MedicoService {

  private static MedicoService instance;

  private MedicoService() {}

  public static MedicoService getInstance() {
    if (instance == null) {
      instance = new MedicoService();
    }
    return instance;
  }

  private final MedicoRepository medicoRepository = MedicoRepository.getInstance();
  private final DepartamentoRepository departamentoRepository = DepartamentoRepository.getInstance();

  public void crearMedico(Long departamentoId, Medico medico) {
    JpaUtil.executeInTransactionVoid(em -> {
      Departamento departamento = departamentoRepository.findById(em, departamentoId)
          .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado"));

      departamento.agregarMedico(medico);
      medicoRepository.create(em, medico);
    });
  }

  public List<Medico> obtenerTodos() {
    return JpaUtil.executeReadOnly(medicoRepository::findAll);
  }

  public List<Medico> obtenerPorDepartamento(Long departamentoId) {
    return JpaUtil.executeReadOnly(em -> medicoRepository.findByDepartamento(em, departamentoId));
  }
}
