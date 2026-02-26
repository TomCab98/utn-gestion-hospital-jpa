package org.example.servicio;

import java.util.List;
import org.example.config.JpaUtil;
import org.example.entidades.Departamento;
import org.example.entidades.Hospital;
import org.example.repositorios.HospitalRepository;

public class HospitalService {

  private static HospitalService instance;

  private HospitalService() {}

  public static HospitalService getInstance() {
    if (instance == null) {
      instance = new HospitalService();
    }
    return instance;
  }

  private final HospitalRepository hospitalRepository = HospitalRepository.getInstance();

  public void crearHospital(Hospital hospital) {
    JpaUtil.executeInTransaction(em -> {
      hospitalRepository.create(em, hospital);
      return hospital;
    });
  }

  public List<Hospital> obtenerTodos() {
    return JpaUtil.executeReadOnly(hospitalRepository::findAll);
  }

  public Hospital obtenerConDepartamentos(Long hospitalId) {
    return JpaUtil.executeReadOnly(
        em -> hospitalRepository.findByIdWithDepartamentos(em, hospitalId)
            .orElseThrow(() -> new IllegalArgumentException("Hospital no encontrado")));
  }

  public Hospital obtenerConDepartamentosYDetalles(Long hospitalId) {
    return JpaUtil.executeReadOnly(
        em -> hospitalRepository.findByIdWithDepartamentosDetalles(em, hospitalId)
            .orElseThrow(() -> new IllegalArgumentException("Hospital no encontrado")));
  }

  public void crearDepartamento(Long hospitalId, Departamento nuevoDepartamento) {
    JpaUtil.executeInTransactionVoid(em -> {
      Hospital hospital = hospitalRepository.findById(em, hospitalId)
          .orElseThrow(() -> new IllegalArgumentException("Hospital no encontrado"));

      hospital.agregarDepartamento(nuevoDepartamento);
    });
  }
}
