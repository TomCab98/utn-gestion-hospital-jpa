package org.example.servicio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.example.entidades.Cita;
import org.example.entidades.Medico;
import org.example.entidades.Paciente;
import org.example.entidades.Sala;
import org.example.excepciones.CitaException;

public interface CitaService {

  Cita programarCita(
      Paciente paciente,
      Medico medico,
      Sala sala,
      LocalDateTime fechaHora,
      BigDecimal costo
  ) throws CitaException;
}
