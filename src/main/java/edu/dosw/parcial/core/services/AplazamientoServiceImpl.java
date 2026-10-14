package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.SolicitudAdopcionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AplazamientoServiceImpl implements AplazamientoService {

    private final SolicitudAdopcionRepository solicitudRepository;

    public AplazamientoServiceImpl(
            SolicitudAdopcionRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public SolicitudAdopcionDocument aplazar(
            String solicitudId,
            String correo,
            LocalDate nuevaFecha) {

        SolicitudAdopcionDocument solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La solicitud de adopción no existe"));

        if (!solicitud.getAdoptanteCorreo().equalsIgnoreCase(correo)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La solicitud no pertenece al adoptante");
        }

        if (solicitud.getEstado() != EstadoSolicitud.APROBADA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud no está aprobada");
        }

        if (solicitud.getAplazamientos() >= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya fue aplazada");
        }

        LocalDate fechaActual = solicitud.getFechaRecibimiento();

        if (nuevaFecha == null
                || fechaActual == null
                || !nuevaFecha.isAfter(fechaActual)
                || nuevaFecha.isAfter(fechaActual.plusDays(15))) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La nueva fecha debe ser posterior a la actual y no superar los 15 días adicionales");
        }

        solicitud.setFechaRecibimiento(nuevaFecha);
        solicitud.setAplazamientos(1);

        return solicitudRepository.save(solicitud);
    }
}
