package de.hskl.ki.db.repository.implementations;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.interfaces.CustomProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class ProjectRepositoryImpl implements CustomProjectRepository {
    public static final String COLLECTION_NAME = "project";
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public List<Project> getProjectByUser(String userName) {
        return mongoOperations.find(Query.query(Criteria.where("username").is(userName)), Project.class, COLLECTION_NAME);
    }

    @Override
    public Project getProjectById(String id) {
        return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), Project.class, COLLECTION_NAME);
    }
}