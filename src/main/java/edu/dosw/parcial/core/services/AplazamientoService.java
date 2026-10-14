package edu.dosw.parcial.core.services;

import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;

import java.time.LocalDate;

public interface AplazamientoService {

    SolicitudAdopcionDocument aplazar(
            String solicitudId,
            String correo,
            LocalDate nuevaFecha);
}
