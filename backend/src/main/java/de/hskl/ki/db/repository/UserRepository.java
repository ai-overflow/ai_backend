package de.hskl.ki.db.repository;

import de.hskl.ki.db.document.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<Users, Integer> {
}
