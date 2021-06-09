package de.hskl.ki.db.repository.interfaces;

import de.hskl.ki.db.document.Page;
import de.hskl.ki.db.document.Project;

public interface CustomPageRepository {
    Page getPageById(String id);
}
