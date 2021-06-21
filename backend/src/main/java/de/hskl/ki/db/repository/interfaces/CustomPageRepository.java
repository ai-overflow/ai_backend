package de.hskl.ki.db.repository.interfaces;

import de.hskl.ki.db.document.Page;

public interface CustomPageRepository {
    Page getPageById(String id);
}
