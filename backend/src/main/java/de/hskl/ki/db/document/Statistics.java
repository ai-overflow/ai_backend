package de.hskl.ki.db.document;

import de.hskl.ki.db.document.helper.StatisticsEntry;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Statistics {
    @BsonProperty("_id")
    @BsonId
    private ObjectId id;
    private List<StatisticsEntry> entries;

    public Statistics() {
    }

    public Statistics(String id, List<StatisticsEntry> entries) {
        this.id = new ObjectId(id);
        this.entries = entries;
    }

    public List<StatisticsEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<StatisticsEntry> entries) {
        this.entries = entries;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "id=" + id +
                ", entries=" + entries +
                '}';
    }
}
