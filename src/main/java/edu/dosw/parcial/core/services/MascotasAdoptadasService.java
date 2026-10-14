package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.response.MascotaAdoptadaResponseDTO;

import java.util.List;

public interface MascotasAdoptadasService {

    List<MascotaAdoptadaResponseDTO> consultar(String correo, boolean esAdmin);
}