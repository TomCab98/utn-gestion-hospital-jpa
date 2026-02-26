package org.example.servicio;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.example.config.JpaUtil;
import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;
import org.example.entidades.enums.EstadoCita;
import org.example.excepciones.CitaException;
import org.example.repositorios.CitaRepository;

public class CitaManager implements CitaService {

  private static final Duration BUFFER = Duration.ofHours(2);

  private static CitaManager instance;

  private final CitaRepository citaRepository = CitaRepository.getInstance();

  private CitaManager() {}

  public static CitaManager getInstance() {
    if (instance == null) {
      instance = new CitaManager();
    }
    return instance;
  }

  @Override
  public Cita programarCita(
      Paciente paciente,
      Medico medico,
      Sala sala,
      LocalDateTime fechaHora,
      BigDecimal costo
  ) throws CitaException {
    validarEntrada(paciente, medico, sala, fechaHora, costo);

    try {
      return JpaUtil.executeInTransaction(em -> {
        Paciente managedPaciente = em.contains(paciente) ? paciente : em.merge(paciente);
        Medico managedMedico = em.contains(medico) ? medico : em.merge(medico);
        Sala managedSala = em.contains(sala) ? sala : em.merge(sala);

        if (managedMedico.getEspecialidad() == null
            || managedSala.getDepartamento() == null
            || !managedMedico.getEspecialidad().equals(managedSala.getDepartamento().getEspecialidad())) {
          throw new IllegalStateException("La especialidad del medico no coincide con el departamento de la sala");
        }

        LocalDateTime desde = fechaHora.minus(BUFFER);
        LocalDateTime hasta = fechaHora.plus(BUFFER);

        List<Cita> colisionesMedico =
            citaRepository.findByMedicoAndFechaBetween(em, managedMedico, desde, hasta);
        if (!colisionesMedico.isEmpty()) {
          throw new IllegalStateException("El medico no esta disponible en ese rango horario");
        }

        List<Cita> colisionesSala =
            citaRepository.findBySalaAndFechaBetween(em, managedSala, desde, hasta);
        if (!colisionesSala.isEmpty()) {
          throw new IllegalStateException("La sala no esta disponible en ese rango horario");
        }

        Cita cita = Cita.builder()
            .paciente(managedPaciente)
            .medico(managedMedico)
            .sala(managedSala)
            .fechaHora(fechaHora)
            .estado(EstadoCita.PROGRAMADA)
            .costo(costo)
            .build();

        managedPaciente.agregarCita(cita);
        managedMedico.agregarCita(cita);
        managedSala.agregarCita(cita);

        citaRepository.create(em, cita);
        return cita;
      });
    } catch (IllegalArgumentException | IllegalStateException e) {
      throw new CitaException(e.getMessage(), e);
    } catch (Exception e) {
      throw new CitaException("Error al programar la cita: " + e.getMessage(), e);
    }
  }

  private void validarEntrada(
      Paciente paciente,
      Medico medico,
      Sala sala,
      LocalDateTime fechaHora,
      BigDecimal costo
  ) throws CitaException {
    if (paciente == null) {
      throw new CitaException("Paciente no puede ser nulo");
    }
    if (medico == null) {
      throw new CitaException("Medico no puede ser nulo");
    }
    if (sala == null) {
      throw new CitaException("Sala no puede ser nula");
    }
    if (fechaHora == null) {
      throw new CitaException("Fecha/hora de la cita es obligatoria");
    }
    if (costo == null) {
      throw new CitaException("Costo es obligatorio");
    }
    if (!fechaHora.isAfter(LocalDateTime.now())) {
      throw new CitaException("La cita debe programarse en una fecha/hora futura");
    }
    if (costo.compareTo(BigDecimal.ZERO) <= 0) {
      throw new CitaException("El costo de la consulta debe ser mayor a cero");
    }
  }
}
