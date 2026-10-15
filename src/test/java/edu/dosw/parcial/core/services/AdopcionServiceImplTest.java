package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.AdoptarMascotaRequestDTO;
import edu.dosw.parcial.core.models.Compatibilidad;
import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.persistence.entities.MascotaDocument;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
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
class AdopcionServiceImplTest {

    private static final String CORREO = "adoptante@dosw.local";

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private SolicitudAdopcionRepository solicitudRepository;

    @InjectMocks
    private AdopcionServiceImpl adopcionService;

    private MascotaDocument mascota;
    private AdoptarMascotaRequestDTO request;

    @BeforeEach
    void setUp() {
        mascota = new MascotaDocument();
        mascota.setId("PERRO-002");
        mascota.setNombre("Bruno");
        mascota.setEstado(EstadoMascota.DISPONIBLE);
        mascota.setCompatibilidad(new Compatibilidad(true, false, false));

        request = new AdoptarMascotaRequestDTO();
        request.setMascotaId("PERRO-002");
        request.setFechaRecibimiento(LocalDate.now().plusDays(5));
        request.setTieneNinos(true);
        request.setTieneOtrasMascotas(false);
        request.setTieneEspaciosReducidos(false);
    }

    private void prepararMascotaDisponibleSinSolicitud() {
        when(mascotaRepository.findById("PERRO-002"))
                .thenReturn(Optional.of(mascota));
        when(solicitudRepository.existsByAdoptanteCorreoIgnoreCaseAndEstado(
                CORREO, EstadoSolicitud.APROBADA)).thenReturn(false);
    }

    private void verificarQueNoSeModificoNada(ResponseStatusException error,
                                              HttpStatus estadoEsperado) {
        assertEquals(estadoEsperado, error.getStatusCode());
        assertEquals(EstadoMascota.DISPONIBLE, mascota.getEstado());
        verify(mascotaRepository, never()).save(any());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void adoptarDeberiaCrearSolicitudAprobadaYDejarMascotaEnProceso() {
        // Arrange
        prepararMascotaDisponibleSinSolicitud();
        when(solicitudRepository.save(any(SolicitudAdopcionDocument.class)))
                .thenAnswer(invocacion -> {
                    SolicitudAdopcionDocument guardada = invocacion.getArgument(0);
                    guardada.setId("SOL-1");
                    return guardada;
                });

        // Act
        SolicitudAdopcionDocument resultado =
                adopcionService.adoptar(request, CORREO);

        // Assert
        assertEquals("SOL-1", resultado.getId());
        assertEquals(EstadoSolicitud.APROBADA, resultado.getEstado());
        assertEquals(CORREO, resultado.getAdoptanteCorreo());
        assertEquals(0, resultado.getAplazamientos());
        assertEquals(EstadoMascota.EN_PROCESO, mascota.getEstado());
        verify(mascotaRepository).save(mascota);
    }

    @Test
    void adoptarDeberiaFallarSiLaMascotaNoExiste() {
        // Arrange
        when(mascotaRepository.findById("PERRO-002"))
                .thenReturn(Optional.empty());

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.NOT_FOUND);
    }

    @Test
    void adoptarDeberiaFallarSiLaMascotaNoEstaDisponible() {
        // Arrange
        mascota.setEstado(EstadoMascota.EN_PROCESO);
        when(mascotaRepository.findById("PERRO-002"))
                .thenReturn(Optional.of(mascota));

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertEquals(EstadoMascota.EN_PROCESO, mascota.getEstado());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void adoptarDeberiaFallarSiYaTieneUnaSolicitudActiva() {
        // Arrange
        when(mascotaRepository.findById("PERRO-002"))
                .thenReturn(Optional.of(mascota));
        when(solicitudRepository.existsByAdoptanteCorreoIgnoreCaseAndEstado(
                CORREO, EstadoSolicitud.APROBADA)).thenReturn(true);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.CONFLICT);
    }

    @Test
    void adoptarDeberiaFallarSiLaFechaEsHoy() {
        // Arrange
        prepararMascotaDisponibleSinSolicitud();
        request.setFechaRecibimiento(LocalDate.now());

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void adoptarDeberiaFallarSiLaFechaSuperaQuinceDias() {
        // Arrange
        prepararMascotaDisponibleSinSolicitud();
        request.setFechaRecibimiento(LocalDate.now().plusDays(16));

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void adoptarDeberiaFallarSiNoHayFecha() {
        // Arrange
        prepararMascotaDisponibleSinSolicitud();
        request.setFechaRecibimiento(null);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void adoptarDeberiaFallarSiFaltaLaDeclaracion() {
        // Arrange
        prepararMascotaDisponibleSinSolicitud();
        request.setTieneNinos(null);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.BAD_REQUEST);
    }

    @Test
    void adoptarDeberiaFallarSiLaMascotaNoTieneCompatibilidad() {
        // Arrange
        mascota.setCompatibilidad(null);
        prepararMascotaDisponibleSinSolicitud();

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.BAD_REQUEST);
    }

    @Test
    void adoptarDeberiaFallarSiNoEsCompatibleConNinos() {
        // Arrange
        mascota.setCompatibilidad(new Compatibilidad(false, true, true));
        prepararMascotaDisponibleSinSolicitud();

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.UNPROCESSABLE_ENTITY);
        assertEquals("La mascota no es compatible con niños", error.getReason());
    }

    @Test
    void adoptarDeberiaFallarSiNoEsCompatibleConOtrasMascotas() {
        // Arrange
        prepararMascotaDisponibleSinSolicitud();
        request.setTieneOtrasMascotas(true);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.UNPROCESSABLE_ENTITY);
        assertEquals("La mascota no es compatible con otras mascotas",
                error.getReason());
    }

    @Test
    void adoptarDeberiaFallarSiNoEsCompatibleConEspaciosReducidos() {
        // Arrange
        prepararMascotaDisponibleSinSolicitud();
        request.setTieneEspaciosReducidos(true);

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> adopcionService.adoptar(request, CORREO));

        // Assert
        verificarQueNoSeModificoNada(error, HttpStatus.UNPROCESSABLE_ENTITY);
        assertEquals("La mascota no es compatible con espacios reducidos",
                error.getReason());
    }
}
