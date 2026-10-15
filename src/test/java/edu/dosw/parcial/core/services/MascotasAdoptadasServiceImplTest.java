package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.response.MascotaAdoptadaResponseDTO;
import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.EstadoSolicitud;
import edu.dosw.parcial.core.models.Tamaño;
import edu.dosw.parcial.persistence.entities.MascotaDocument;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.SolicitudAdopcionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MascotasAdoptadasServiceImplTest {

    private static final String CORREO = "adoptante@dosw.local";

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private SolicitudAdopcionRepository solicitudRepository;

    @InjectMocks
    private MascotasAdoptadasServiceImpl mascotasAdoptadasService;

    private SolicitudAdopcionDocument solicitud(String id, String mascotaId,
                                                LocalDate fecha) {
        SolicitudAdopcionDocument solicitud = new SolicitudAdopcionDocument();
        solicitud.setId(id);
        solicitud.setMascotaId(mascotaId);
        solicitud.setAdoptanteCorreo(CORREO);
        solicitud.setFechaRecibimiento(fecha);
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        return solicitud;
    }

    private MascotaDocument mascota(String id, String nombre) {
        MascotaDocument mascota = new MascotaDocument();
        mascota.setId(id);
        mascota.setNombre(nombre);
        mascota.setEspecie(Especie.GATO);
        mascota.setEdad(18);
        mascota.setTamano(Tamaño.PEQUENO);
        mascota.setRefugioId("REFUGIO-NORTE");
        return mascota;
    }

    @Test
    void consultarComoAdminDeberiaOrdenarPorFechaDescendente() {
        // Arrange
        LocalDate hoy = LocalDate.now();
        when(solicitudRepository.findByEstado(EstadoSolicitud.APROBADA))
                .thenReturn(List.of(
                        solicitud("SOL-1", "GATO-001", hoy.plusDays(2)),
                        solicitud("SOL-2", "GATO-002", hoy.plusDays(9))));
        when(mascotaRepository.findById("GATO-001"))
                .thenReturn(Optional.of(mascota("GATO-001", "Luna")));
        when(mascotaRepository.findById("GATO-002"))
                .thenReturn(Optional.of(mascota("GATO-002", "Milo")));

        // Act
        List<MascotaAdoptadaResponseDTO> resultado =
                mascotasAdoptadasService.consultar(CORREO, true);

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("SOL-2", resultado.get(0).getSolicitudId());
        assertEquals("Milo", resultado.get(0).getNombre());
        assertEquals("GATO", resultado.get(0).getEspecie());
        assertEquals("PEQUENO", resultado.get(0).getTamano());
        assertEquals("SOL-1", resultado.get(1).getSolicitudId());
        verify(solicitudRepository, never())
                .findByAdoptanteCorreoIgnoreCaseAndEstado(any(), any());
    }

    @Test
    void consultarComoAdoptanteDeberiaBuscarSoloSusSolicitudes() {
        // Arrange
        when(solicitudRepository.findByAdoptanteCorreoIgnoreCaseAndEstado(
                CORREO, EstadoSolicitud.APROBADA))
                .thenReturn(List.of(solicitud(
                        "SOL-1", "GATO-001", LocalDate.now().plusDays(3))));
        when(mascotaRepository.findById("GATO-001"))
                .thenReturn(Optional.of(mascota("GATO-001", "Luna")));

        // Act
        List<MascotaAdoptadaResponseDTO> resultado =
                mascotasAdoptadasService.consultar(CORREO, false);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("GATO-001", resultado.get(0).getMascotaId());
        verify(solicitudRepository, never()).findByEstado(any());
    }

    @Test
    void consultarDeberiaOmitirSolicitudesSinMascota() {
        // Arrange
        when(solicitudRepository.findByEstado(EstadoSolicitud.APROBADA))
                .thenReturn(List.of(solicitud(
                        "SOL-1", "NO-EXISTE", LocalDate.now().plusDays(3))));
        when(mascotaRepository.findById("NO-EXISTE"))
                .thenReturn(Optional.empty());

        // Act
        List<MascotaAdoptadaResponseDTO> resultado =
                mascotasAdoptadasService.consultar(CORREO, true);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void consultarSinSolicitudesDeberiaRetornarListaVacia() {
        // Arrange
        when(solicitudRepository.findByEstado(EstadoSolicitud.APROBADA))
                .thenReturn(List.of());

        // Act
        List<MascotaAdoptadaResponseDTO> resultado =
                mascotasAdoptadasService.consultar(CORREO, true);

        // Assert
        assertTrue(resultado.isEmpty());
    }
}
