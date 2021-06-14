package de.hskl.ki.db.document;

import de.hskl.ki.models.page.PageCreationRequest;
import de.hskl.ki.models.page.PageLayout;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
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
    private Date creationDate = new Date(System.currentTimeMillis());
    private PageLayout pageLayout;

    public Page(boolean active, String title, String description, List<Project> selectedProjects, Map<String, List<String>> topLevelInput, PageLayout pageLayout) {
        this.active = active;
        this.title = title;
        this.description = description;
        this.selectedProjects = selectedProjects;
        this.topLevelInput = topLevelInput;
        this.pageLayout = pageLayout;
    }

    public Page(PageCreationRequest request) {
        this.active = request.isActive();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.topLevelInput = request.getTopLevelInput();
        this.pageLayout = request.getPageLayout();
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public PageLayout getPageLayout() {
        return pageLayout;
    }

    public void setPageLayout(PageLayout pageLayout) {
        this.pageLayout = pageLayout;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id='" + id + '\'' +
                ", active=" + active +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", selectedProjects=" + selectedProjects +
                ", topLevelInput=" + topLevelInput +
                ", creationDate=" + creationDate +
                ", pageLayout=" + pageLayout +
                '}';
    }
}
