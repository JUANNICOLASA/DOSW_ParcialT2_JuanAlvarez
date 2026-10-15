package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.AdoptarMascotaRequestDTO;
import edu.dosw.parcial.core.services.AdopcionService;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/adopciones")
public class AdopcionController {

    private final AdopcionService adopcionService;

    public AdopcionController(AdopcionService adopcionService) {
        this.adopcionService = adopcionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudAdopcionDocument adoptar(
            @Valid @RequestBody AdoptarMascotaRequestDTO request,
            Authentication authentication) {
        return adopcionService.adoptar(
                request, authentication.getName());
    }
}
