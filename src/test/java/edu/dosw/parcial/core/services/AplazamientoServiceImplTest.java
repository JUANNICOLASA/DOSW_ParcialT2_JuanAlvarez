package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.SolicitudAdopcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AplazamientoServiceImplTest {

    private static final String CORREO = "adoptante@dosw.local";

    @Mock
    private SolicitudAdopcionRepository solicitudRepository;

    @InjectMocks
    private AplazamientoServiceImpl aplazamientoService;

    private SolicitudAdopcionDocument solicitud;
    private LocalDate fechaActual;

    @BeforeEach
    void setUp() {
        fechaActual = LocalDate.now().plusDays(5);
        solicitud = new SolicitudAdopcionDocument();
        solicitud.setId("SOL-1");
        solicitud.setMascotaId("PERRO-002");
        solicitud.setAdoptanteCorreo(CORREO);
        solicitud.setFechaRecibimiento(fechaActual);
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setAplazamientos(0);
    }

    private void verificarSinCambios(ResponseStatusException error,
                                     HttpStatus estadoEsperado) {
        assertEquals(estadoEsperado, error.getStatusCode());
        assertEquals(fechaActual, solicitud.getFechaRecibimiento());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void aplazarDeberiaActualizarFechaYContarElAplazamiento() {
        // Arrange
        LocalDate nuevaFecha = fechaActual.plusDays(10);
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(solicitud)).thenReturn(solicitud);

        // Act
        SolicitudAdopcionDocument resultado =
                aplazamientoService.aplazar("SOL-1", CORREO, nuevaFecha);

        // Assert
        assertEquals(nuevaFecha, resultado.getFechaRecibimiento());
        assertEquals(1, resultado.getAplazamientos());
        verify(solicitudRepository).save(solicitud);
    }

    @Test
    void aplazarDeberiaFallarSiLaSolicitudNoExiste() {
        // Arrange
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.empty());
        LocalDate nuevaFecha = fechaActual.plusDays(2);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> aplazamientoService.aplazar(
                        "SOL-1", CORREO, nuevaFecha));

        // Assert
        verificarSinCambios(error, HttpStatus.NOT_FOUND);
    }

    @Test
    void aplazarDeberiaFallarSiLaSolicitudEsDeOtroAdoptante() {
        // Arrange
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.of(solicitud));
        LocalDate nuevaFecha = fechaActual.plusDays(2);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> aplazamientoService.aplazar(
                        "SOL-1", "otro@dosw.local", nuevaFecha));

        // Assert
        verificarSinCambios(error, HttpStatus.FORBIDDEN);
    }

    @Test
    void aplazarDeberiaFallarSiLaSolicitudNoEstaAprobada() {
        // Arrange
        solicitud.setEstado(null);
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.of(solicitud));
        LocalDate nuevaFecha = fechaActual.plusDays(2);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> aplazamientoService.aplazar(
                        "SOL-1", CORREO, nuevaFecha));

        // Assert
        verificarSinCambios(error, HttpStatus.CONFLICT);
    }

    @Test
    void aplazarDeberiaConservarFechaSiYaFueAplazada() {
        // Arrange
        solicitud.setAplazamientos(1);
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.of(solicitud));
        LocalDate nuevaFecha = fechaActual.plusDays(2);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> aplazamientoService.aplazar(
                        "SOL-1", CORREO, nuevaFecha));

        // Assert
        verificarSinCambios(error, HttpStatus.CONFLICT);
        assertEquals(1, solicitud.getAplazamientos());
    }

    @Test
    void aplazarDeberiaFallarSiLaNuevaFechaNoEsPosterior() {
        // Arrange
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.of(solicitud));

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> aplazamientoService.aplazar("SOL-1", CORREO, fechaActual));

        // Assert
        verificarSinCambios(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void aplazarDeberiaFallarSiSuperaQuinceDiasAdicionales() {
        // Arrange
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.of(solicitud));
        LocalDate nuevaFecha = fechaActual.plusDays(16);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> aplazamientoService.aplazar(
                        "SOL-1", CORREO, nuevaFecha));

        // Assert
        verificarSinCambios(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void aplazarDeberiaFallarSiNoSeEnviaFecha() {
        // Arrange
        when(solicitudRepository.findById("SOL-1"))
                .thenReturn(Optional.of(solicitud));

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> aplazamientoService.aplazar("SOL-1", CORREO, null));

        // Assert
        verificarSinCambios(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
