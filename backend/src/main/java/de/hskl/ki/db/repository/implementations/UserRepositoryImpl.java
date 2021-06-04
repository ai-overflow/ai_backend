package de.hskl.ki.db.repository.implementations;

import de.hskl.ki.db.document.Users;
import de.hskl.ki.db.repository.interfaces.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UserRepositoryImpl implements CustomUserRepository {
    public static final String COLLECTION_NAME = "users";
    @Autowired
    private MongoOperations mongoOperations;

    public Users findUserByName(String userName) {
        return mongoOperations.findOne(Query.query(Criteria.where("username").is(userName)), Users.class, COLLECTION_NAME);
    }
}
