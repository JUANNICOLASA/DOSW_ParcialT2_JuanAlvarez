package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.AplazarRecibimientoRequestDTO;
import edu.dosw.parcial.core.services.AplazamientoService;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/adopciones")
public class AplazamientoController {

    private final AplazamientoService aplazamientoService;

    public AplazamientoController(
            AplazamientoService aplazamientoService) {
        this.aplazamientoService = aplazamientoService;
    }

    @PatchMapping("/{solicitudId}/aplazar")
    @ResponseStatus(HttpStatus.OK)
    public SolicitudAdopcionDocument aplazar(
            @PathVariable String solicitudId,
            @Valid @RequestBody AplazarRecibimientoRequestDTO request,
            Authentication authentication) {

        return aplazamientoService.aplazar(
                solicitudId,
                authentication.getName(),
                request.getNuevaFecha());
    }
}
