package edu.dosw.parcial.controller.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MENSAJE = "mensaje";

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException exception) {

        Map<String, String> errores = new LinkedHashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }

        logger.warn("Solicitud rechazada por datos inválidos: {}", errores);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put(MENSAJE, "Los datos enviados no son válidos");
        respuesta.put("errores", errores);

        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarFormatoInvalido(
            HttpMessageNotReadableException exception) {

        logger.warn("Solicitud rechazada: formato JSON o enumerado inválido");

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put(MENSAJE,
                "El JSON contiene un valor inválido o tiene un formato incorrecto");

        return ResponseEntity.badRequest().body(respuesta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarArgumentoInvalido(
            IllegalArgumentException exception) {

        logger.warn("Operación rechazada: {}", exception.getMessage());

        HttpStatus estado = HttpStatus.UNPROCESSABLE_ENTITY;

        if ("La mascota ya existe".equals(exception.getMessage())) {
            estado = HttpStatus.CONFLICT;
        } else if ("El refugio no existe".equals(exception.getMessage())) {
            estado = HttpStatus.NOT_FOUND;
        }

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put(MENSAJE, exception.getMessage());

        return ResponseEntity.status(estado).body(respuesta);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> manejarEstadoHTTP(
            ResponseStatusException exception) {

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put(MENSAJE, exception.getReason() == null
                ? "No fue posible completar la solicitud"
                : exception.getReason());

        return ResponseEntity.status(exception.getStatusCode()).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErrorGeneral(
            Exception exception) {

        logger.error("Error inesperado al procesar la solicitud", exception);

        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put(MENSAJE, "Ocurrió un error interno del servidor");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(respuesta);
    }
}
