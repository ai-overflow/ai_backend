package de.hskl.ki.db.repository;

import de.hskl.ki.db.document.Statistics;
import de.hskl.ki.db.repository.interfaces.CustomStatisticsRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticsRepository extends MongoRepository<Statistics, String>, CustomStatisticsRepository {
}
