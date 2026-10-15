package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.response.MascotaAdoptadaResponseDTO;
import edu.dosw.parcial.core.services.MascotasAdoptadasService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mascotas-adoptadas")
public class MascotasAdoptadasController {

    private final MascotasAdoptadasService mascotasAdoptadasService;

    public MascotasAdoptadasController(
            MascotasAdoptadasService mascotasAdoptadasService) {
        this.mascotasAdoptadasService = mascotasAdoptadasService;
    }

    @GetMapping
    public List<MascotaAdoptadaResponseDTO> consultar(
            Authentication authentication) {

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(autoridad ->
                        autoridad.getAuthority().equals("ROLE_ADMIN"));

        return mascotasAdoptadasService.consultar(
                authentication.getName(), esAdmin);
    }
}