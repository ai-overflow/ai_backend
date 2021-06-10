package de.hskl.ki.db.repository;

import de.hskl.ki.db.document.Page;
import de.hskl.ki.db.repository.interfaces.CustomPageRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends MongoRepository<Page, String>, CustomPageRepository {

}
