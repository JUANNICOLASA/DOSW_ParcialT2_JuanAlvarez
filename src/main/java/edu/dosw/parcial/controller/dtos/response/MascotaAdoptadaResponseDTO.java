package edu.dosw.parcial.controller.dtos.response;

import java.time.LocalDate;

public class MascotaAdoptadaResponseDTO {

    private String solicitudId;
    private String mascotaId;
    private String nombre;
    private String especie;
    private int edad;
    private String tamano;
    private String refugioId;
    private LocalDate fechaRecibimiento;

    public String getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(String solicitudId) {
        this.solicitudId = solicitudId;
    }

    public String getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(String mascotaId) {
        this.mascotaId = mascotaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getRefugioId() {
        return refugioId;
    }

    public void setRefugioId(String refugioId) {
        this.refugioId = refugioId;
    }

    public LocalDate getFechaRecibimiento() {
        return fechaRecibimiento;
    }

    public void setFechaRecibimiento(LocalDate fechaRecibimiento) {
        this.fechaRecibimiento = fechaRecibimiento;
    }
}
