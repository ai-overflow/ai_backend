package de.hskl.ki.db.document.helper;

import java.sql.Timestamp;
import java.util.Date;

public class StatisticsEntry {
    private Date timestamp;
    private long executionTimeMs;

    public StatisticsEntry() {
    }

    public StatisticsEntry(Timestamp timestamp, long executionTimeMs) {
        this.timestamp = timestamp;
        this.executionTimeMs = executionTimeMs;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    @Override
    public String toString() {
        return "StatisticsEntry{" +
                "timestamp=" + timestamp +
                ", executionTimeMs=" + executionTimeMs +
                '}';
    }
}
