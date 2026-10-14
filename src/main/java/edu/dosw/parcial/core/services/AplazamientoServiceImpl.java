package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.SolicitudAdopcionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AplazamientoServiceImpl implements AplazamientoService {

    private static final Logger log = LoggerFactory.getLogger(AplazamientoServiceImpl.class);

    private final SolicitudAdopcionRepository solicitudRepository;

    public AplazamientoServiceImpl(
            SolicitudAdopcionRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public SolicitudAdopcionDocument aplazar(
            String solicitudId,
            String correo,
            LocalDate nuevaFecha) {

        log.info("Iniciando aplazamiento de la solicitud {}", solicitudId);

        SolicitudAdopcionDocument solicitud = solicitudRepository
                .findById(solicitudId)
                .orElseThrow(() -> {
                    log.warn("No existe la solicitud {}", solicitudId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "La solicitud de adopción no existe");
                });

        if (!solicitud.getAdoptanteCorreo().equalsIgnoreCase(correo)) {
            log.warn("El usuario {} no es propietario de la solicitud {}", correo, solicitudId);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La solicitud no pertenece al adoptante");
        }

        if (solicitud.getEstado() != EstadoSolicitud.APROBADA) {
            log.warn("La solicitud {} no está aprobada", solicitudId);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud no está aprobada");
        }

        if (solicitud.getAplazamientos() >= 1) {
            log.warn("La solicitud {} ya fue aplazada", solicitudId);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya fue aplazada");
        }

        LocalDate fechaActual = solicitud.getFechaRecibimiento();

        if (nuevaFecha == null
                || fechaActual == null
                || !nuevaFecha.isAfter(fechaActual)
                || nuevaFecha.isAfter(fechaActual.plusDays(15))) {
            log.warn("Nueva fecha inválida para la solicitud {}", solicitudId);
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La nueva fecha debe ser posterior a la actual y no superar los 15 días adicionales");
        }

        solicitud.setFechaRecibimiento(nuevaFecha);
        solicitud.setAplazamientos(1);

        SolicitudAdopcionDocument actualizada = solicitudRepository.save(solicitud);

        log.info("Solicitud {} aplazada hasta {}", solicitudId, nuevaFecha);

        return actualizada;
    }
}

