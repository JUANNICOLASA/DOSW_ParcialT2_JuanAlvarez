package edu.dosw.parcial.persistence.entities;

import edu.dosw.parcial.core.models.EstadoSolicitud;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "solicitudes_adopcion")
public class SolicitudAdopcionDocument {

    @Id
    private String id;
    private String mascotaId;
    private String adoptanteCorreo;
    private LocalDate fechaRecibimiento;
    private EstadoSolicitud estado;
    private int aplazamientos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(String mascotaId) {
        this.mascotaId = mascotaId;
    }

    public String getAdoptanteCorreo() {
        return adoptanteCorreo;
    }

    public void setAdoptanteCorreo(String adoptanteCorreo) {
        this.adoptanteCorreo = adoptanteCorreo;
    }

    public LocalDate getFechaRecibimiento() {
        return fechaRecibimiento;
    }

    public void setFechaRecibimiento(LocalDate fechaRecibimiento) {
        this.fechaRecibimiento = fechaRecibimiento;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public int getAplazamientos() {
        return aplazamientos;
    }

    public void setAplazamientos(int aplazamientos) {
        this.aplazamientos = aplazamientos;
    }
}
