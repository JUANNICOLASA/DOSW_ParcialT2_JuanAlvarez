package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.response.MascotaAdoptadaResponseDTO;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.MascotaDocument;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.SolicitudAdopcionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class MascotasAdoptadasServiceImpl implements MascotasAdoptadasService {

    private static final Logger log = LoggerFactory.getLogger(MascotasAdoptadasServiceImpl.class);

    private final MascotaRepository mascotaRepository;
    private final SolicitudAdopcionRepository solicitudRepository;

    public MascotasAdoptadasServiceImpl(
            MascotaRepository mascotaRepository,
            SolicitudAdopcionRepository solicitudRepository) {
        this.mascotaRepository = mascotaRepository;
        this.solicitudRepository = solicitudRepository;
    }

    public List<MascotaAdoptadaResponseDTO> consultar(
            String correo, boolean esAdmin) {

        log.info("Consultando mascotas adoptadas. Usuario administrador: {}", esAdmin);

        List<SolicitudAdopcionDocument> solicitudes;

        if (esAdmin) {
            solicitudes = solicitudRepository.findByEstado(
                    EstadoSolicitud.APROBADA);
        } else {
            solicitudes = solicitudRepository
                    .findByAdoptanteCorreoIgnoreCaseAndEstado(
                            correo, EstadoSolicitud.APROBADA);
        }

        List<MascotaAdoptadaResponseDTO> resultado = solicitudes.stream()
                .sorted(Comparator.comparing(
                        SolicitudAdopcionDocument::getFechaRecibimiento,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::convertir)
                .filter(dto -> dto != null)
                .toList();

        log.info("Consulta finalizada. Mascotas encontradas: {}", resultado.size());

        return resultado;
    }

    private MascotaAdoptadaResponseDTO convertir(
            SolicitudAdopcionDocument solicitud) {

        MascotaDocument mascota = mascotaRepository
                .findById(solicitud.getMascotaId())
                .orElse(null);

        if (mascota == null) {
            log.warn("No se encontró la mascota asociada a la solicitud {}",
                    solicitud.getId());
            return null;
        }

        MascotaAdoptadaResponseDTO dto =
                new MascotaAdoptadaResponseDTO();

        dto.setSolicitudId(solicitud.getId());
        dto.setMascotaId(mascota.getId());
        dto.setNombre(mascota.getNombre());
        dto.setEspecie(mascota.getEspecie().name());
        dto.setEdad(mascota.getEdad());
        dto.setTamano(mascota.getTamano().name());
        dto.setRefugioId(mascota.getRefugioId());
        dto.setFechaRecibimiento(solicitud.getFechaRecibimiento());

        return dto;
    }
}
