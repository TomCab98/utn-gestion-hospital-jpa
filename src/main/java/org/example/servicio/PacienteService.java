package org.example.servicio;

import java.util.List;
import org.example.config.JpaUtil;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.repositorios.HospitalRepository;
import org.example.repositorios.PacienteRepository;

public class PacienteService {
  private static PacienteService instance;

  private final HospitalRepository hospitalRepository = HospitalRepository.getInstance();
  private final PacienteRepository pacienteRepository = PacienteRepository.getInstance();

  private PacienteService() {}

  public static PacienteService getInstance() {
    if (instance == null) {
      instance = new PacienteService();
    }
    return instance;
  }

  public void crearPaciente(Long hospitalId, Paciente paciente) {
    JpaUtil.executeInTransactionVoid(em -> {
      Hospital hospital = hospitalRepository.findById(em, hospitalId)
          .orElseThrow(() -> new IllegalArgumentException("Hospital no encontrado"));

      hospital.agregarPaciente(paciente);
      pacienteRepository.create(em, paciente);
    });
  }

  public List<Paciente> obtenerPorHospital(Long hospitalId) {
    return JpaUtil.executeReadOnly(em -> pacienteRepository.obtenerPorHospital(em, hospitalId));
  }
}
