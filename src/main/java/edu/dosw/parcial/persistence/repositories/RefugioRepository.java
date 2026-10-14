package edu.dosw.parcial.persistence.repositories;

import edu.dosw.parcial.persistence.entities.RefugioDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefugioRepository extends MongoRepository<RefugioDocument, String> {
}
