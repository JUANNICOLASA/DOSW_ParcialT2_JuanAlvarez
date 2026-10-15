package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.AdoptarMascotaRequestDTO;
import edu.dosw.parcial.controller.dtos.request.AplazarRecibimientoRequestDTO;
import edu.dosw.parcial.controller.dtos.request.CrearMascotaRequestDTO;
import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.controller.dtos.response.MascotaAdoptadaResponseDTO;
import edu.dosw.parcial.core.models.Compatibilidad;
import edu.dosw.parcial.core.models.Especie;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.models.Tamaño;
import edu.dosw.parcial.core.services.AdopcionService;
import edu.dosw.parcial.core.services.AplazamientoService;
import edu.dosw.parcial.core.services.AutenticacionService;
import edu.dosw.parcial.core.services.MascotaService;
import edu.dosw.parcial.core.services.MascotasAdoptadasService;
import edu.dosw.parcial.persistence.entities.SolicitudAdopcionDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControladoresTest {

    @Mock
    private MascotaService mascotaService;

    @Mock
    private AdopcionService adopcionService;

    @Mock
    private AplazamientoService aplazamientoService;

    @Mock
    private MascotasAdoptadasService mascotasAdoptadasService;

    @Mock
    private AutenticacionService autenticacionService;

    private Authentication usuario(String correo, String rol) {
        return new UsernamePasswordAuthenticationToken(
                correo, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
    }

    private CrearMascotaRequestDTO crearMascotaRequest() {
        CrearMascotaRequestDTO request = new CrearMascotaRequestDTO();
        request.setId("PERRO-001");
        request.setNombre("Max");
        request.setEspecie(Especie.PERRO);
        request.setEdad(24);
        request.setTamano(Tamaño.MEDIANO);
        request.setRefugioId("REFUGIO-NORTE");
        request.setCompatibilidad(new Compatibilidad(true, true, true));
        return request;
    }

    @Test
    void crearMascotaDeberiaDelegarAlServicio() {
        // Arrange
        MascotaController controller = new MascotaController(mascotaService);
        when(mascotaService.crearMascota(any(Mascota.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        Mascota resultado = controller.crearMascota(crearMascotaRequest());

        // Assert
        assertEquals("PERRO-001", resultado.getId());
        assertEquals("REFUGIO-NORTE", resultado.getRefugioId());
    }

    @Test
    void crearMascotaDeberiaTraducirLosErroresDelServicio() {
        // Arrange
        MascotaController controller = new MascotaController(mascotaService);
        when(mascotaService.crearMascota(any(Mascota.class)))
                .thenThrow(new IllegalArgumentException("La mascota ya existe"))
                .thenThrow(new IllegalArgumentException("El refugio no existe"))
                .thenThrow(new IllegalArgumentException("El lugar debe ser un refugio"));

        CrearMascotaRequestDTO request = crearMascotaRequest();

        // Act
        ResponseStatusException duplicada = assertThrows(
                ResponseStatusException.class,
                () -> controller.crearMascota(request));
        ResponseStatusException sinRefugio = assertThrows(
                ResponseStatusException.class,
                () -> controller.crearMascota(request));
        ResponseStatusException noEsRefugio = assertThrows(
                ResponseStatusException.class,
                () -> controller.crearMascota(request));

        // Assert
        assertEquals(HttpStatus.CONFLICT, duplicada.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, sinRefugio.getStatusCode());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, noEsRefugio.getStatusCode());
    }

    @Test
    void adoptarDeberiaUsarElCorreoDelUsuarioAutenticado() {
        // Arrange
        AdopcionController controller = new AdopcionController(adopcionService);
        AdoptarMascotaRequestDTO request = new AdoptarMascotaRequestDTO();
        SolicitudAdopcionDocument solicitud = new SolicitudAdopcionDocument();
        when(adopcionService.adoptar(request, "adoptante@dosw.local"))
                .thenReturn(solicitud);

        // Act
        SolicitudAdopcionDocument resultado = controller.adoptar(
                request, usuario("adoptante@dosw.local", "ADOPTANTE"));

        // Assert
        assertSame(solicitud, resultado);
    }

    @Test
    void aplazarDeberiaEnviarLaNuevaFechaAlServicio() {
        // Arrange
        AplazamientoController controller =
                new AplazamientoController(aplazamientoService);
        AplazarRecibimientoRequestDTO request = new AplazarRecibimientoRequestDTO();
        LocalDate nuevaFecha = LocalDate.now().plusDays(8);
        request.setNuevaFecha(nuevaFecha);
        SolicitudAdopcionDocument solicitud = new SolicitudAdopcionDocument();
        when(aplazamientoService.aplazar(
                "SOL-1", "adoptante@dosw.local", nuevaFecha)).thenReturn(solicitud);

        // Act
        SolicitudAdopcionDocument resultado = controller.aplazar(
                "SOL-1", request, usuario("adoptante@dosw.local", "ADOPTANTE"));

        // Assert
        assertSame(solicitud, resultado);
    }

    @Test
    void consultarAdoptadasDeberiaIndicarSiEsAdmin() {
        // Arrange
        MascotasAdoptadasController controller =
                new MascotasAdoptadasController(mascotasAdoptadasService);
        List<MascotaAdoptadaResponseDTO> lista =
                List.of(new MascotaAdoptadaResponseDTO());
        when(mascotasAdoptadasService.consultar("admin@dosw.local", true))
                .thenReturn(lista);
        when(mascotasAdoptadasService.consultar("adoptante@dosw.local", false))
                .thenReturn(List.of());

        // Act
        List<MascotaAdoptadaResponseDTO> comoAdmin = controller.consultar(
                usuario("admin@dosw.local", "ADMIN"));
        List<MascotaAdoptadaResponseDTO> comoAdoptante = controller.consultar(
                usuario("adoptante@dosw.local", "ADOPTANTE"));

        // Assert
        assertEquals(1, comoAdmin.size());
        assertEquals(0, comoAdoptante.size());
    }

    @Test
    void iniciarSesionDeberiaRetornarElToken() {
        // Arrange
        AutenticacionController controller =
                new AutenticacionController(autenticacionService);
        LoginRequestDTO request = new LoginRequestDTO();
        LoginResponseDTO respuesta =
                new LoginResponseDTO("token", "admin@dosw.local", "ADMIN");
        when(autenticacionService.iniciarSesion(request)).thenReturn(respuesta);

        // Act
        LoginResponseDTO resultado = controller.iniciarSesion(request);

        // Assert
        assertSame(respuesta, resultado);
        verify(autenticacionService).iniciarSesion(request);
    }

    @Test
    void iniciarSesionDeberiaResponder401ConCredencialesInvalidas() {
        // Arrange
        AutenticacionController controller =
                new AutenticacionController(autenticacionService);
        LoginRequestDTO request = new LoginRequestDTO();
        when(autenticacionService.iniciarSesion(request))
                .thenThrow(new BadCredentialsException("Correo o contraseña incorrectos"));

        // Act
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> controller.iniciarSesion(request));

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }
}
