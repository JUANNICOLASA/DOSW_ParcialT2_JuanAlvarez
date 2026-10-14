package edu.dosw.parcial.controller.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AplazarRecibimientoRequestDTO {

    @NotNull(message = "La nueva fecha es obligatoria")
    private LocalDate nuevaFecha;

    public AplazarRecibimientoRequestDTO() {
    }

    public LocalDate getNuevaFecha() {
        return nuevaFecha;
    }

    public void setNuevaFecha(LocalDate nuevaFecha) {
        this.nuevaFecha = nuevaFecha;
    }
}
