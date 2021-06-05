package de.hskl.ki.resource;

import de.hskl.ki.db.document.Projects;
import de.hskl.ki.db.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/p/")
public class ProjectResource {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("project")
    public List<Projects> getAll() {
        return projectRepository.findAll();
    }
}
