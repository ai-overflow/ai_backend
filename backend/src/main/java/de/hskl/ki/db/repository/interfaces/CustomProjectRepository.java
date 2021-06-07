package de.hskl.ki.db.repository.interfaces;

import de.hskl.ki.db.document.Projects;

import java.util.List;

public interface CustomProjectRepository {
    List<Projects> getProjectByUser(String username);

    Projects getProjectById(String id);
}
