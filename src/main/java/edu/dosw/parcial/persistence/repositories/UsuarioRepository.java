package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UsuarioRepository
        extends MongoRepository<UsuarioDocument, String> {

    Optional<UsuarioDocument> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);
}
