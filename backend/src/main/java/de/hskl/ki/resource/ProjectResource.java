package de.hskl.ki.resource;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.git.GitCreationRequest;
import de.hskl.ki.services.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/p/")
public class ProjectResource {
    @Autowired
    private GitService gitService;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("project")
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    @PostMapping("project")
    public ResponseEntity<Project> cloneRepo(@RequestBody GitCreationRequest repo) {
        var gitServiceResponse = gitService.generateProject(repo);
        return ResponseEntity.ok(gitServiceResponse);
    }

    @DeleteMapping("project/{id}")
    public ResponseEntity<String> deleteRepo(@PathVariable String id) {
        gitService.deleteProject(id);
        return ResponseEntity.ok("ok");
    }
}
