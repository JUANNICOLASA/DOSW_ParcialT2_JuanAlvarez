package edu.dosw.parcial.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRETO = "clave-de-pruebas-con-mas-de-32-caracteres";

    @Test
    void generarTokenDeberiaIncluirCorreoYRol() {
        // Arrange
        JwtService jwtService = new JwtService(SECRETO, 60000);

        // Act
        String token = jwtService.generarToken("admin@dosw.local", "ADMIN");
        Claims claims = jwtService.validarToken(token);

        // Assert
        assertEquals("admin@dosw.local", claims.getSubject());
        assertEquals("ADMIN", claims.get("rol", String.class));
    }

    @Test
    void validarTokenDeberiaFallarConFirmaDeOtraClave() {
        // Arrange
        JwtService emisor = new JwtService(SECRETO, 60000);
        JwtService receptor = new JwtService(
                "otra-clave-distinta-con-mas-de-32-caracteres", 60000);
        String token = emisor.generarToken("admin@dosw.local", "ADMIN");

        // Act y Assert
        assertThrows(JwtException.class, () -> receptor.validarToken(token));
    }

    @Test
    void validarTokenDeberiaFallarSiExpiro() {
        // Arrange
        JwtService jwtService = new JwtService(SECRETO, -1000);
        String token = jwtService.generarToken("admin@dosw.local", "ADMIN");

        // Act y Assert
        assertThrows(JwtException.class, () -> jwtService.validarToken(token));
    }

    @Test
    void constructorDeberiaRechazarSecretoCorto() {
        // Act
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService("corta", 60000));

        // Assert
        assertEquals("JWT_SECRET debe tener al menos 32 caracteres",
                error.getMessage());
    }

    @Test
    void constructorDeberiaRechazarSecretoNulo() {
        // Act y Assert
        assertThrows(IllegalArgumentException.class,
                () -> new JwtService(null, 60000));
    }
}
