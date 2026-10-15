
package edu.dosw.parcial.config;

import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioInicializador implements ApplicationRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(UsuarioInicializador.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminCorreo;
    private final String adminPassword;
    private final String adoptanteCorreo;
    private final String adoptantePassword;

    public UsuarioInicializador(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.usuarios.admin-correo:admin@dosw.local}")
            String adminCorreo,
            @Value("${app.usuarios.admin-password:Admin12345!}")
            String adminPassword,
            @Value("${app.usuarios.adoptante-correo:adoptante@dosw.local}")
            String adoptanteCorreo,
            @Value("${app.usuarios.adoptante-password:Adoptante12345!}")
            String adoptantePassword) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminCorreo = adminCorreo;
        this.adminPassword = adminPassword;
        this.adoptanteCorreo = adoptanteCorreo;
        this.adoptantePassword = adoptantePassword;
    }

    public void run(ApplicationArguments args) {
        crearSiNoExiste("admin", adminCorreo, adminPassword, "ADMIN");
        crearSiNoExiste(
                "adoptante", adoptanteCorreo, adoptantePassword, "ADOPTANTE");
    }

    private void crearSiNoExiste(
            String id, String correo, String password, String rol) {

        if (!usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            UsuarioDocument usuario = new UsuarioDocument(
                    id,
                    correo,
                    passwordEncoder.encode(password),
                    rol);

            usuarioRepository.save(usuario);
            logger.info("Usuario inicial creado para el rol {}", rol);
        }
    }
}
