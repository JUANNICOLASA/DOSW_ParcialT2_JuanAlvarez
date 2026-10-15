package edu.dosw.parcial.config;

import edu.dosw.parcial.core.models.TipoRefugio;
import edu.dosw.parcial.persistence.entities.RefugioDocument;
import edu.dosw.parcial.persistence.repositories.RefugioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RefugioInicializador implements ApplicationRunner {

    private static final Logger logger =
            LoggerFactory.getLogger(RefugioInicializador.class);

    private final RefugioRepository refugioRepository;

    public RefugioInicializador(RefugioRepository refugioRepository) {
        this.refugioRepository = refugioRepository;
    }

    public void run(ApplicationArguments args) {
        if (!refugioRepository.existsById("REFUGIO-NORTE")) {
            RefugioDocument refugio = new RefugioDocument();
            refugio.setId("REFUGIO-NORTE");
            refugio.setNombre("Refugio Norte");
            refugio.setTipo(TipoRefugio.REFUGIO);
            refugioRepository.save(refugio);
            logger.info("Refugio inicial creado");
        }
    }
}

