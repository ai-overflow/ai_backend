package de.hskl.ki.db.repository.interfaces;

import de.hskl.ki.db.document.Statistics;
import de.hskl.ki.db.document.helper.StatisticsEntry;

import java.util.Optional;

public interface CustomStatisticsRepository {
    void addEntry(String id, StatisticsEntry statisticsEntry);
    Optional<Statistics> findById(String id, Integer limit);
}
