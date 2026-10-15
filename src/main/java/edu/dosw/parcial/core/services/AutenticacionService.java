package edu.dosw.parcial.core.services;

import edu.dosw.parcial.auth.JwtService;
import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

    private static final Logger logger =
            LoggerFactory.getLogger(AutenticacionService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticacionService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO iniciarSesion(LoginRequestDTO request) {
        UsuarioDocument usuario = usuarioRepository
                .findByCorreoIgnoreCase(request.getCorreo().trim())
                .orElseThrow(() -> {
                    logger.warn("Intento de login con correo no registrado");
                    return new BadCredentialsException(
                            "Correo o contraseña incorrectos");
                });

        if (!passwordEncoder.matches(
                request.getPassword(), usuario.getPassword())) {
            logger.warn("Contraseña incorrecta para {}",
                    usuario.getCorreo());

            throw new BadCredentialsException(
                    "Correo o contraseña incorrectos");
        }

        String token = jwtService.generarToken(
                usuario.getCorreo(), usuario.getRol());

        logger.info("Login exitoso para usuario con rol {}",
                usuario.getRol());

        return new LoginResponseDTO(
                token, usuario.getCorreo(), usuario.getRol());
    }
}
