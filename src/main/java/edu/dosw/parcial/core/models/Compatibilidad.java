package edu.dosw.parcial.core.models;

import jakarta.validation.constraints.NotNull;

public class Compatibilidad {

    @NotNull(message = "Debe indicar la compatibilidad con niños")
    private Boolean conNinos;

    @NotNull(message = "Debe indicar la compatibilidad con otras mascotas")
    private Boolean conOtrasMascotas;

    @NotNull(message = "Debe indicar la compatibilidad con espacios reducidos")
    private Boolean conEspaciosReducidos;

    public Compatibilidad() {
    }

    public Compatibilidad(Boolean conNinos, Boolean conOtrasMascotas,
                          Boolean conEspaciosReducidos) {
        this.conNinos = conNinos;
        this.conOtrasMascotas = conOtrasMascotas;
        this.conEspaciosReducidos = conEspaciosReducidos;
    }

    public Boolean getConNinos() {
        return conNinos;
    }

    public void setConNinos(Boolean conNinos) {
        this.conNinos = conNinos;
    }

    public Boolean getConOtrasMascotas() {
        return conOtrasMascotas;
    }

    public void setConOtrasMascotas(Boolean conOtrasMascotas) {
        this.conOtrasMascotas = conOtrasMascotas;
    }

    public Boolean getConEspaciosReducidos() {
        return conEspaciosReducidos;
    }

    public void setConEspaciosReducidos(Boolean conEspaciosReducidos) {
        this.conEspaciosReducidos = conEspaciosReducidos;
    }
}
