package de.hskl.ki.db.repository;

import de.hskl.ki.db.document.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private MongoOperations mongoOperations;

    public static final String COLLECTION_NAME = "users";

    public Users findUserByName(String userName) {
        return mongoOperations.findOne(Query.query(Criteria.where("username").is(userName)), Users.class, COLLECTION_NAME);
    }

    public void save(Users user) {
        mongoOperations.save(user);
    }

    public List<Users> findAll() {
        return mongoOperations.findAll(Users.class);
    }

    public Users findById(Integer id) {
        return mongoOperations.findById(id, Users.class);
    }
}
