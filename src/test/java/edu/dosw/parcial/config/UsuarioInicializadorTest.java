package edu.dosw.parcial.config;

import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioInicializadorTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioInicializador inicializador;

    @BeforeEach
    void setUp() {
        inicializador = new UsuarioInicializador(
                usuarioRepository, passwordEncoder,
                "admin@dosw.local", "Admin12345!",
                "adoptante@dosw.local", "Adoptante12345!");
    }

    @Test
    void deberiaCrearLosUsuariosSiNoExisten() {
        // Arrange
        when(usuarioRepository.existsByCorreoIgnoreCase(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hash");

        // Act
        inicializador.run(null);

        // Assert
        ArgumentCaptor<UsuarioDocument> captor =
                ArgumentCaptor.forClass(UsuarioDocument.class);
        verify(usuarioRepository, times(2)).save(captor.capture());
        List<UsuarioDocument> guardados = captor.getAllValues();
        assertEquals("ADMIN", guardados.get(0).getRol());
        assertEquals("ADOPTANTE", guardados.get(1).getRol());
        assertEquals("hash", guardados.get(1).getPassword());
    }

    @Test
    void noDeberiaDuplicarUsuariosExistentes() {
        // Arrange
        when(usuarioRepository.existsByCorreoIgnoreCase(any())).thenReturn(true);

        // Act
        inicializador.run(null);

        // Assert
        verify(usuarioRepository, never()).save(any());
    }
}
