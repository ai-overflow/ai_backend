package de.hskl.ki.models.page;

import java.util.List;
import java.util.Map;

public class PageCreationRequest {
    private boolean active;
    private String title;
    private String description;
    private List<String> selectedProjects;
    private Map<String, List<String>> topLevelInput;
    private PageLayout pageLayout;

    public PageCreationRequest() {
    }

    public PageCreationRequest(boolean active, String title, String description, List<String> selectedProjects, Map<String, List<String>> topLevelInput, PageLayout pageLayout) {
        this.active = active;
        this.title = title;
        this.description = description;
        this.selectedProjects = selectedProjects;
        this.topLevelInput = topLevelInput;
        this.pageLayout = pageLayout;
    }

    public PageLayout getPageLayout() {
        return pageLayout;
    }

    public void setPageLayout(PageLayout pageLayout) {
        this.pageLayout = pageLayout;
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

    public List<String> getSelectedProjects() {
        return selectedProjects;
    }

    public void setSelectedProjects(List<String> selectedProjects) {
        this.selectedProjects = selectedProjects;
    }

    public Map<String, List<String>> getTopLevelInput() {
        return topLevelInput;
    }

    public void setTopLevelInput(Map<String, List<String>> topLevelInput) {
        this.topLevelInput = topLevelInput;
    }
}
