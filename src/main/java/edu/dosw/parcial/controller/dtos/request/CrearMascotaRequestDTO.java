package edu.dosw.parcial.controller.dtos.request;

import edu.dosw.parcial.core.models.Compatibilidad;
import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.Tamaño;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearMascotaRequestDTO {

    @NotBlank(message = "El ID es obligatorio")
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "La especie es obligatoria")
    private Especie especie;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 360, message = "La edad máxima es de 360 meses")
    private Integer edad;

    @NotNull(message = "El tamaño es obligatorio")
    private Tamaño tamano;

    @NotBlank(message = "El ID del refugio es obligatorio")
    private String refugioId;

    @Valid
    @NotNull(message = "La compatibilidad es obligatoria")
    private Compatibilidad compatibilidad;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Tamaño getTamano() {
        return tamano;
    }

    public void setTamano(Tamaño tamano) {
        this.tamano = tamano;
    }

    public String getRefugioId() {
        return refugioId;
    }

    public void setRefugioId(String refugioId) {
        this.refugioId = refugioId;
    }

    public Compatibilidad getCompatibilidad() {
        return compatibilidad;
    }

    public void setCompatibilidad(Compatibilidad compatibilidad) {
        this.compatibilidad = compatibilidad;
    }
}
