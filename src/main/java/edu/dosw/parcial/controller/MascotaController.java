package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.CrearMascotaRequestDTO;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.services.MascotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mascota crearMascota(@Valid @RequestBody CrearMascotaRequestDTO request) {
        Mascota mascota = new Mascota(
                request.getId(),
                request.getNombre(),
                request.getEspecie(),
                request.getEdad(),
                request.getTamano(),
                request.getRefugioId(),
                request.getCompatibilidad(),
                null
        );

        try {
            return mascotaService.crearMascota(mascota);
        } catch (IllegalArgumentException e) {
            String mensaje = e.getMessage();

            if ("La mascota ya existe".equals(mensaje)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
            }

            if ("El refugio no existe".equals(mensaje)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, mensaje);
            }

            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, mensaje);
        }
    }
}
