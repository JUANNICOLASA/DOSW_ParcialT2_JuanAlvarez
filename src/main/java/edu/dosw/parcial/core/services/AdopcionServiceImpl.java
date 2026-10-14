package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.AdoptarMascotaRequestDTO;
import edu.dosw.parcial.core.models.Compatibilidad;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.MascotaDocument;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.SolicitudAdopcionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AdopcionServiceImpl implements AdopcionService {

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

        MascotaDocument mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La mascota no existe"));

        if (mascota.getEstado() != EstadoMascota.DISPONIBLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "La mascota no está disponible");
        }

        if (solicitudRepository.existsByAdoptanteCorreoIgnoreCaseAndEstado(
                correo, EstadoSolicitud.APROBADA)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El adoptante ya tiene una solicitud activa");
        }

        LocalDate fecha = request.getFechaRecibimiento();

        if (fecha == null
                || fecha.isBefore(LocalDate.now().plusDays(1))
                || fecha.isAfter(LocalDate.now().plusDays(15))) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La fecha debe estar entre 1 y 15 días desde hoy");
        }

        Compatibilidad compatibilidad = mascota.getCompatibilidad();

        if (compatibilidad == null
                || request.getTieneNinos() == null
                || request.getTieneOtrasMascotas() == null
                || request.getTieneEspaciosReducidos() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe completar la información de compatibilidad");
        }

        if (Boolean.TRUE.equals(request.getTieneNinos())
                && !Boolean.TRUE.equals(compatibilidad.getConNinos())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La mascota no es compatible con niños");
        }

        if (Boolean.TRUE.equals(request.getTieneOtrasMascotas())
                && !Boolean.TRUE.equals(compatibilidad.getConOtrasMascotas())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "La mascota no es compatible con otras mascotas");
        }

        if (Boolean.TRUE.equals(request.getTieneEspaciosReducidos())
                && !Boolean.TRUE.equals(compatibilidad.getConEspaciosReducidos())) {
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

        return solicitudRepository.save(solicitud);
    }
}
