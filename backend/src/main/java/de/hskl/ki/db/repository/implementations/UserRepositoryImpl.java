package de.hskl.ki.db.repository.implementations;

import de.hskl.ki.db.document.User;
import de.hskl.ki.db.repository.interfaces.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UserRepositoryImpl implements CustomUserRepository {
    public static final String COLLECTION_NAME = "user";
    @Autowired
    private MongoOperations mongoOperations;

    public User findUserByName(String userName) {
        return mongoOperations.findOne(Query.query(Criteria.where("username").is(userName)), User.class, COLLECTION_NAME);
    }
}
