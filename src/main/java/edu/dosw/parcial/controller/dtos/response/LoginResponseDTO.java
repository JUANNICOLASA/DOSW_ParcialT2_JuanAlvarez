package edu.dosw.parcial.controller.dtos.response;

public class LoginResponseDTO {

    private String token;
    private String tipo;
    private String correo;
    private String rol;

    public LoginResponseDTO(String token, String correo, String rol) {
        this.token = token;
        this.tipo = "Bearer";
        this.correo = correo;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }
}
