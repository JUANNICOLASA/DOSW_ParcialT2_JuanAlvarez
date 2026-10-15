package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.AdoptarMascotaRequestDTO;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;

public interface AdopcionService {

    SolicitudAdopcionDocument adoptar(
            AdoptarMascotaRequestDTO request,
            String correo);
}