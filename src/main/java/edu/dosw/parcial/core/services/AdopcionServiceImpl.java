package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.AdoptarMascotaRequestDTO;
import edu.dosw.parcial.core.models.Compatibilidad;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.MascotaDocument;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.SolicitudAdopcionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AdopcionServiceImpl implements AdopcionService {

    private static final Logger log = LoggerFactory.getLogger(AdopcionServiceImpl.class);

    private final MascotaRepository mascotaRepository;
    private final SolicitudAdopcionRepository solicitudRepository;

    public AdopcionServiceImpl(
            MascotaRepository mascotaRepository,
            SolicitudAdopcionRepository solicitudRepository) {
        this.mascotaRepository = mascotaRepository;
        this.solicitudRepository = solicitudRepository;
    }

    public SolicitudAdopcionDocument adoptar(
            AdoptarMascotaRequestDTO request,
            String correo) {

        log.info("Iniciando solicitud de adopción para el usuario {}", correo);

        MascotaDocument mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> {
                    log.warn("No existe la mascota con ID {}", request.getMascotaId());
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "La mascota no existe");
                });

        if (mascota.getEstado() != EstadoMascota.DISPONIBLE) {
            log.warn("La mascota {} no está disponible", mascota.getId());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La mascota no está disponible");
        }

        if (solicitudRepository.existsByAdoptanteCorreoIgnoreCaseAndEstado(
                correo, EstadoSolicitud.APROBADA)) {
            log.warn("El usuario {} ya tiene una solicitud activa", correo);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El adoptante ya tiene una solicitud activa");
        }

        LocalDate fecha = request.getFechaRecibimiento();

        if (fecha == null
                || fecha.isBefore(LocalDate.now().plusDays(1))
                || fecha.isAfter(LocalDate.now().plusDays(15))) {
            log.warn("Fecha de recibimiento inválida para la mascota {}", mascota.getId());
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La fecha debe estar entre 1 y 15 días desde hoy");
        }

        Compatibilidad compatibilidad = mascota.getCompatibilidad();

        if (compatibilidad == null
                || request.getTieneNinos() == null
                || request.getTieneOtrasMascotas() == null
                || request.getTieneEspaciosReducidos() == null) {
            log.warn("Información de compatibilidad incompleta para la mascota {}", mascota.getId());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe completar la información de compatibilidad");
        }

        if (Boolean.TRUE.equals(request.getTieneNinos())
                && !Boolean.TRUE.equals(compatibilidad.getConNinos())) {
            log.warn("La mascota {} no es compatible con niños", mascota.getId());
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La mascota no es compatible con niños");
        }

        if (Boolean.TRUE.equals(request.getTieneOtrasMascotas())
                && !Boolean.TRUE.equals(compatibilidad.getConOtrasMascotas())) {
            log.warn("La mascota {} no es compatible con otras mascotas", mascota.getId());
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La mascota no es compatible con otras mascotas");
        }

        if (Boolean.TRUE.equals(request.getTieneEspaciosReducidos())
                && !Boolean.TRUE.equals(compatibilidad.getConEspaciosReducidos())) {
            log.warn("La mascota {} no es compatible con espacios reducidos", mascota.getId());
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La mascota no es compatible con espacios reducidos");
        }

        SolicitudAdopcionDocument solicitud = new SolicitudAdopcionDocument();
        solicitud.setMascotaId(mascota.getId());
        solicitud.setAdoptanteCorreo(correo);
        solicitud.setFechaRecibimiento(fecha);
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setAplazamientos(0);

        mascota.setEstado(EstadoMascota.EN_PROCESO);
        mascotaRepository.save(mascota);

        SolicitudAdopcionDocument guardada = solicitudRepository.save(solicitud);

        log.info("Solicitud de adopción {} creada para la mascota {}",
                guardada.getId(), mascota.getId());

        return guardada;
    }
}
