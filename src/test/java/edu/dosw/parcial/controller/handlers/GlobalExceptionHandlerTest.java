package edu.dosw.parcial.controller.handlers;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void manejarValidacionDeberiaListarLosCamposInvalidos() {
        // Arrange
        BeanPropertyBindingResult resultado =
                new BeanPropertyBindingResult(new Object(), "request");
        resultado.addError(new FieldError(
                "request", "edad", "La edad máxima es de 360 meses"));
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        mock(MethodParameter.class), resultado);

        // Act
        ResponseEntity<Map<String, Object>> respuesta =
                handler.manejarValidacion(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("Los datos enviados no son válidos",
                respuesta.getBody().get("mensaje"));
        assertEquals(Map.of("edad", "La edad máxima es de 360 meses"),
                respuesta.getBody().get("errores"));
    }

    @Test
    void manejarFormatoInvalidoDeberiaResponder400() {
        // Arrange
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "JSON inválido", new MockHttpInputMessage(new byte[0]));

        // Act
        ResponseEntity<Map<String, String>> respuesta =
                handler.manejarFormatoInvalido(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    }

    @Test
    void manejarArgumentoInvalidoDeberiaResponder409SiLaMascotaExiste() {
        // Act
        ResponseEntity<Map<String, String>> respuesta =
                handler.manejarArgumentoInvalido(
                        new IllegalArgumentException("La mascota ya existe"));

        // Assert
        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals("La mascota ya existe", respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarArgumentoInvalidoDeberiaResponder404SiNoHayRefugio() {
        // Act
        ResponseEntity<Map<String, String>> respuesta =
                handler.manejarArgumentoInvalido(
                        new IllegalArgumentException("El refugio no existe"));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
    }

    @Test
    void manejarArgumentoInvalidoDeberiaResponder422EnOtrosCasos() {
        // Act
        ResponseEntity<Map<String, String>> respuesta =
                handler.manejarArgumentoInvalido(
                        new IllegalArgumentException("El lugar debe ser un refugio"));

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, respuesta.getStatusCode());
    }

    @Test
    void manejarEstadoHTTPDeberiaConservarCodigoYMensaje() {
        // Act
        ResponseEntity<Map<String, String>> respuesta =
                handler.manejarEstadoHTTP(new ResponseStatusException(
                        HttpStatus.CONFLICT, "La mascota no está disponible"));

        // Assert
        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
        assertEquals("La mascota no está disponible",
                respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarEstadoHTTPSinMotivoDeberiaUsarMensajeGenerico() {
        // Act
        ResponseEntity<Map<String, String>> respuesta =
                handler.manejarEstadoHTTP(
                        new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Assert
        assertEquals("No fue posible completar la solicitud",
                respuesta.getBody().get("mensaje"));
    }

    @Test
    void manejarErrorGeneralNoDeberiaExponerElDetalle() {
        // Act
        ResponseEntity<Map<String, String>> respuesta =
                handler.manejarErrorGeneral(
                        new RuntimeException("conexión rechazada a MongoDB"));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertEquals("Ocurrió un error interno del servidor",
                respuesta.getBody().get("mensaje"));
    }
}
