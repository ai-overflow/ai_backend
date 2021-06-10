package de.hskl.ki.db.repository;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.interfaces.CustomProjectRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String>, CustomProjectRepository {

}
