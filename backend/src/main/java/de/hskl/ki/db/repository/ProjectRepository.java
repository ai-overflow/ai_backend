package de.hskl.ki.db.repository;

import de.hskl.ki.db.document.Projects;
import de.hskl.ki.db.document.Users;
import de.hskl.ki.db.repository.interfaces.CustomProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Projects, Integer>, CustomProjectRepository {

}
