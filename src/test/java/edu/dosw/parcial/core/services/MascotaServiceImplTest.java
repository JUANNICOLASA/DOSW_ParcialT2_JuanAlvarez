package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.Compatibilidad;
import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.models.Tamaño;
import edu.dosw.parcial.core.models.TipoRefugio;
import edu.dosw.parcial.persistence.entities.MascotaDocument;
import edu.dosw.parcial.persistence.entities.RefugioDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.RefugioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private RefugioRepository refugioRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Mascota mascota;

    @BeforeEach
    void setUp() {
        mascota = new Mascota("PERRO-001", "Max", Especie.PERRO, 24,
                Tamaño.MEDIANO, "REFUGIO-NORTE",
                new Compatibilidad(true, true, true), null);
    }

    private RefugioDocument refugio(TipoRefugio tipo) {
        RefugioDocument refugio = new RefugioDocument();
        refugio.setId("REFUGIO-NORTE");
        refugio.setNombre("Refugio Norte");
        refugio.setTipo(tipo);
        return refugio;
    }

    @Test
    void crearMascotaDeberiaGuardarlaComoDisponible() {
        // Arrange
        when(mascotaRepository.existsById("PERRO-001")).thenReturn(false);
        when(refugioRepository.findById("REFUGIO-NORTE"))
                .thenReturn(Optional.of(refugio(TipoRefugio.REFUGIO)));

        // Act
        Mascota resultado = mascotaService.crearMascota(mascota);

        // Assert
        assertEquals(EstadoMascota.DISPONIBLE, resultado.getEstado());
        ArgumentCaptor<MascotaDocument> captor =
                ArgumentCaptor.forClass(MascotaDocument.class);
        verify(mascotaRepository).save(captor.capture());
        assertEquals("PERRO-001", captor.getValue().getId());
        assertEquals("Max", captor.getValue().getNombre());
        assertEquals(EstadoMascota.DISPONIBLE, captor.getValue().getEstado());
    }

    @Test
    void crearMascotaDeberiaFallarSiElIdYaExiste() {
        // Arrange
        when(mascotaRepository.existsById("PERRO-001")).thenReturn(true);

        // Act
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> mascotaService.crearMascota(mascota));

        // Assert
        assertEquals("La mascota ya existe", error.getMessage());
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void crearMascotaDeberiaFallarSiElRefugioNoExiste() {
        // Arrange
        when(mascotaRepository.existsById("PERRO-001")).thenReturn(false);
        when(refugioRepository.findById("REFUGIO-NORTE"))
                .thenReturn(Optional.empty());

        // Act
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> mascotaService.crearMascota(mascota));

        // Assert
        assertEquals("El refugio no existe", error.getMessage());
        verify(mascotaRepository, never()).save(any());
    }

    @Test
    void crearMascotaDeberiaFallarSiElNodoNoEsRefugio() {
        // Arrange
        when(mascotaRepository.existsById("PERRO-001")).thenReturn(false);
        when(refugioRepository.findById("REFUGIO-NORTE"))
                .thenReturn(Optional.of(refugio(TipoRefugio.CIUDAD)));

        // Act
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> mascotaService.crearMascota(mascota));

        // Assert
        assertEquals("El lugar debe ser un refugio", error.getMessage());
        verify(mascotaRepository, never()).save(any());
    }
}
