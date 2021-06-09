package de.hskl.ki.db.repository.implementations;

import de.hskl.ki.db.document.Page;
import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.interfaces.CustomPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class PageRepositoryImpl implements CustomPageRepository {
    public static final String COLLECTION_NAME = "page";
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public Page getPageById(String id) {
        return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), Page.class, COLLECTION_NAME);
    }
}