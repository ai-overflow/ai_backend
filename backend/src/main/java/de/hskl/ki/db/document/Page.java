package de.hskl.ki.db.document;

import de.hskl.ki.models.page.PageCreationRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document
public class Page {

    @Id
    private String id;
    private boolean active;
    private String title;
    private String description;
    @DBRef
    private List<Project> selectedProjects;
    private Map<String, List<String>> topLevelInput;

    public Page(boolean active, String title, String description, List<Project> selectedProjects, Map<String, List<String>> topLevelInput) {
        this.active = active;
        this.title = title;
        this.description = description;
        this.selectedProjects = selectedProjects;
        this.topLevelInput = topLevelInput;
    }

    public Page(PageCreationRequest request) {
        this.active = request.isActive();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.topLevelInput = request.getTopLevelInput();
    }

    public Page() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Project> getSelectedProjects() {
        return selectedProjects;
    }

    public void setSelectedProjects(List<Project> selectedProjects) {
        this.selectedProjects = selectedProjects;
    }

    public Map<String, List<String>> getTopLevelInput() {
        return topLevelInput;
    }

    public void setTopLevelInput(Map<String, List<String>> topLevelInput) {
        this.topLevelInput = topLevelInput;
    }
}
