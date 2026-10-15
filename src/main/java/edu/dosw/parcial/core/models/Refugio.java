package edu.dosw.parcial.core.models;

public class Refugio {

    private String id;
    private String nombre;
    private TipoRefugio tipo;

    public Refugio() {
    }

    public Refugio(String id, String nombre, TipoRefugio tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
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

    public TipoRefugio getTipo() {
        return tipo;
    }

    public void setTipo(TipoRefugio tipo) {
        this.tipo = tipo;
    }

}