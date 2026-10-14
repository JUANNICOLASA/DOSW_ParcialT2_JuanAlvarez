package edu.dosw.parcial.core.models;

public class Mascota {

    private String id;
    private String nombre;
    private Especie especie;
    private int edad;
    private Tamaño tamano;
    private String refugioId;
    private Compatibilidad compatibilidad;
    private EstadoMascota estado;

    public Mascota() {
    }

    public Mascota(String id, String nombre, Especie especie, int edad,
                   Tamaño tamano, String refugioId,
                   Compatibilidad compatibilidad, EstadoMascota estado) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.edad = edad;
        this.tamano = tamano;
        this.refugioId = refugioId;
        this.compatibilidad = compatibilidad;
        this.estado = estado;
    }

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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
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

    public EstadoMascota getEstado() {
        return estado;
    }

    public void setEstado(EstadoMascota estado) {
        this.estado = estado;
    }

}