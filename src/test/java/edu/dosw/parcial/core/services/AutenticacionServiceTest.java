package edu.dosw.parcial.core.services;

import edu.dosw.parcial.auth.JwtService;
import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AutenticacionService autenticacionService;

    private LoginRequestDTO request;
    private UsuarioDocument usuario;

    @BeforeEach
    void setUp() {
        request = new LoginRequestDTO();
        request.setCorreo(" admin@dosw.local ");
        request.setPassword("Admin12345!");

        usuario = new UsuarioDocument(
                "admin", "admin@dosw.local", "hash", "ADMIN");
    }

    @Test
    void iniciarSesionDeberiaRetornarTokenConCredencialesValidas() {
        // Arrange
        when(usuarioRepository.findByCorreoIgnoreCase("admin@dosw.local"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Admin12345!", "hash")).thenReturn(true);
        when(jwtService.generarToken("admin@dosw.local", "ADMIN"))
                .thenReturn("token-jwt");

        // Act
        LoginResponseDTO respuesta = autenticacionService.iniciarSesion(request);

        // Assert
        assertEquals("token-jwt", respuesta.getToken());
        assertEquals("Bearer", respuesta.getTipo());
        assertEquals("admin@dosw.local", respuesta.getCorreo());
        assertEquals("ADMIN", respuesta.getRol());
    }

    @Test
    void iniciarSesionDeberiaFallarSiElCorreoNoExiste() {
        // Arrange
        when(usuarioRepository.findByCorreoIgnoreCase("admin@dosw.local"))
                .thenReturn(Optional.empty());

        // Act
        BadCredentialsException error = assertThrows(
                BadCredentialsException.class,
                () -> autenticacionService.iniciarSesion(request));

        // Assert
        assertEquals("Correo o contraseña incorrectos", error.getMessage());
        verify(jwtService, never()).generarToken(any(), any());
    }

    @Test
    void iniciarSesionDeberiaFallarSiLaContrasenaEsIncorrecta() {
        // Arrange
        when(usuarioRepository.findByCorreoIgnoreCase("admin@dosw.local"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Admin12345!", "hash")).thenReturn(false);

        // Act
        BadCredentialsException error = assertThrows(
                BadCredentialsException.class,
                () -> autenticacionService.iniciarSesion(request));

        // Assert
        assertEquals("Correo o contraseña incorrectos", error.getMessage());
        verify(jwtService, never()).generarToken(any(), any());
    }
}
