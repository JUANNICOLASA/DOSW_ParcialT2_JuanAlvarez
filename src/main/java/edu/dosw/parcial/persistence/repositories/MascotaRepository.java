package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.MascotaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MascotaRepository extends MongoRepository<MascotaDocument, String> {
}