package edu.dosw.parcial.persistence.entities;

import edu.dosw.parcial.core.models.TipoRefugio;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "refugios")
public class RefugioDocument {

    @Id
    private String id;
    private String nombre;
    private TipoRefugio tipo;

    public RefugioDocument() {
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
