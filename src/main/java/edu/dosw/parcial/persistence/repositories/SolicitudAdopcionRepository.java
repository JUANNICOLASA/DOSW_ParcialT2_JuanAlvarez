package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SolicitudAdopcionRepository
        extends MongoRepository<SolicitudAdopcionDocument, String> {

    boolean existsByAdoptanteCorreoIgnoreCaseAndEstado(
            String correo, EstadoSolicitud estado);

    List<SolicitudAdopcionDocument> findByAdoptanteCorreoIgnoreCaseAndEstado(
            String correo, EstadoSolicitud estado);

    List<SolicitudAdopcionDocument> findByEstado(EstadoSolicitud estado);
}
