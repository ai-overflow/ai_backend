package de.hskl.ki.db.repository;

import de.hskl.ki.db.document.User;
import de.hskl.ki.db.repository.interfaces.CustomUserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, Integer>, CustomUserRepository {

}
