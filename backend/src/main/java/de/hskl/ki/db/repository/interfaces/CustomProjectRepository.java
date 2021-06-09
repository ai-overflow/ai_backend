package de.hskl.ki.db.repository.interfaces;

import de.hskl.ki.db.document.Project;

import java.util.List;

public interface CustomProjectRepository {
    List<Project> getProjectByUser(String username);

    Project getProjectById(String id);
}
