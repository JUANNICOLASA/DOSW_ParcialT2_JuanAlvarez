package edu.dosw.parcial.controller.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AdoptarMascotaRequestDTO {

    @NotBlank(message = "El ID de la mascota es obligatorio")
    private String mascotaId;

    @NotNull(message = "La fecha de recibimiento es obligatoria")
    private LocalDate fechaRecibimiento;

    @NotNull(message = "Debe indicar si tiene niños")
    private Boolean tieneNinos;

    @NotNull(message = "Debe indicar si tiene otras mascotas")
    private Boolean tieneOtrasMascotas;

    @NotNull(message = "Debe indicar si tiene espacios reducidos")
    private Boolean tieneEspaciosReducidos;

    public String getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(String mascotaId) {
        this.mascotaId = mascotaId;
    }

    public LocalDate getFechaRecibimiento() {
        return fechaRecibimiento;
    }

    public void setFechaRecibimiento(LocalDate fechaRecibimiento) {
        this.fechaRecibimiento = fechaRecibimiento;
    }

    public Boolean getTieneNinos() {
        return tieneNinos;
    }

    public void setTieneNinos(Boolean tieneNinos) {
        this.tieneNinos = tieneNinos;
    }

    public Boolean getTieneOtrasMascotas() {
        return tieneOtrasMascotas;
    }

    public void setTieneOtrasMascotas(Boolean tieneOtrasMascotas) {
        this.tieneOtrasMascotas = tieneOtrasMascotas;
    }

    public Boolean getTieneEspaciosReducidos() {
        return tieneEspaciosReducidos;
    }

    public void setTieneEspaciosReducidos(Boolean tieneEspaciosReducidos) {
        this.tieneEspaciosReducidos = tieneEspaciosReducidos;
    }
}

