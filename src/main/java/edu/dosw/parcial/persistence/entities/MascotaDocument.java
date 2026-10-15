package edu.dosw.parcial.persistence.entities;

import edu.dosw.parcial.core.models.Compatibilidad;
import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.Tamaño;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mascotas")
public class MascotaDocument {

    @Id
    private String id;
    private String nombre;
    private Especie especie;
    private int edad;
    private Tamaño tamano;
    private String refugioId;
    private Compatibilidad compatibilidad;
    private EstadoMascota estado;

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