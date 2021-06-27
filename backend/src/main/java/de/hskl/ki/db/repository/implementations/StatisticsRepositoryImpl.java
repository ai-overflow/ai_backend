package de.hskl.ki.db.repository.implementations;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import de.hskl.ki.db.document.Statistics;
import de.hskl.ki.db.document.helper.StatisticsEntry;
import de.hskl.ki.db.repository.interfaces.CustomStatisticsRepository;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Optional;

public class StatisticsRepositoryImpl implements CustomStatisticsRepository {
    public static final String COLLECTION_NAME = "statistics";
    @Autowired
    private MongoOperations mongoOperations;


    @Override
    public void addEntry(String id, StatisticsEntry statisticsEntry) {
        if (!mongoOperations.exists(Query.query(Criteria.where("id").is(id)), Statistics.class)) {
            mongoOperations.save(new Statistics(id, new ArrayList<>()));
        }

        mongoOperations.updateFirst(
                Query.query(Criteria.where("id").is(id)),
                new Update().push("entries", statisticsEntry),
                Statistics.class,
                COLLECTION_NAME);
    }

    public Optional<Statistics> findById(String id, Integer limit) {
        var res = mongoOperations.getCollection(COLLECTION_NAME)
                .find(Filters.eq("_id", new ObjectId(id)), Statistics.class)
                .projection(Projections.fields(new Document("entries", new Document("$slice", limit))))
                .first();

        if (res != null)
            return Optional.of(res);
        else
            return Optional.empty();
    }
}
