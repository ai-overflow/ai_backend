package de.hskl.ki.db.repository.implementations;

import de.hskl.ki.db.document.Projects;
import de.hskl.ki.db.repository.interfaces.CustomProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class ProjectRepositoryImpl implements CustomProjectRepository {
    public static final String COLLECTION_NAME = "projects";
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public List<Projects> getProjectByUser(String userName) {
        return mongoOperations.find(Query.query(Criteria.where("username").is(userName)), Projects.class, COLLECTION_NAME);
    }

    @Override
    public Projects getProjectById(String id) {
        return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), Projects.class, COLLECTION_NAME);
    }
}