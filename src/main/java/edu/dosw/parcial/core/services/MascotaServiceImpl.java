
package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.EstadoMascota;
import edu.dosw.parcial.core.models.Mascota;
import edu.dosw.parcial.core.models.TipoRefugio;
import edu.dosw.parcial.persistence.entities.MascotaDocument;
import edu.dosw.parcial.persistence.entities.RefugioDocument;
import edu.dosw.parcial.persistence.repositories.MascotaRepository;
import edu.dosw.parcial.persistence.repositories.RefugioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MascotaServiceImpl implements MascotaService {

    private static final Logger logger =
            LoggerFactory.getLogger(MascotaServiceImpl.class);

    private final MascotaRepository mascotaRepository;
    private final RefugioRepository refugioRepository;

    public MascotaServiceImpl(MascotaRepository mascotaRepository,
                              RefugioRepository refugioRepository) {
        this.mascotaRepository = mascotaRepository;
        this.refugioRepository = refugioRepository;
    }

    public Mascota crearMascota(Mascota mascota) {
        logger.info("Iniciando creación de mascota con ID {}",
                mascota.getId());

        if (mascotaRepository.existsById(mascota.getId())) {
            logger.warn("No se creó la mascota: el ID {} ya existe",
                    mascota.getId());
            throw new IllegalArgumentException("La mascota ya existe");
        }

        RefugioDocument refugio = refugioRepository
                .findById(mascota.getRefugioId())
                .orElseThrow(() -> {
                    logger.warn("No existe el refugio {}",
                            mascota.getRefugioId());
                    return new IllegalArgumentException("El refugio no existe");
                });

        if (refugio.getTipo() != TipoRefugio.REFUGIO) {
            logger.warn("El lugar {} no es de tipo REFUGIO",
                    mascota.getRefugioId());
            throw new IllegalArgumentException(
                    "El lugar debe ser un refugio");
        }

        mascota.setEstado(EstadoMascota.DISPONIBLE);

        MascotaDocument documento = new MascotaDocument();
        documento.setId(mascota.getId());
        documento.setNombre(mascota.getNombre());
        documento.setEspecie(mascota.getEspecie());
        documento.setEdad(mascota.getEdad());
        documento.setTamano(mascota.getTamano());
        documento.setRefugioId(mascota.getRefugioId());
        documento.setCompatibilidad(mascota.getCompatibilidad());
        documento.setEstado(mascota.getEstado());

        mascotaRepository.save(documento);

        logger.info("Mascota {} creada correctamente con estado {}",
                mascota.getId(), mascota.getEstado());

        return mascota;
    }
}

