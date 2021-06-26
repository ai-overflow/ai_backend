package de.hskl.ki.resource;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.project.ProjectChangeRequest;
import de.hskl.ki.models.project.ProjectCreationRequest;
import de.hskl.ki.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/p/")
public class ProjectResource {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("project")
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    @GetMapping("project/{id}")
    public ResponseEntity<Project> getProject(@PathVariable String id) {
        var project = projectRepository.findById(id);
        return project.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("project")
    public ResponseEntity<Project> cloneRepo(@RequestBody ProjectCreationRequest repo) {
        var gitServiceResponse = projectService.generateProject(repo.getRepoUrl());
        return ResponseEntity.ok(gitServiceResponse);
    }

    @DeleteMapping("project/{id}")
    public ResponseEntity<String> deleteRepo(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("project/{id}/triton")
    public ResponseEntity<String> removeTritonModels(@PathVariable String id) {
        projectService.removeTritonModels(id);
        return ResponseEntity.ok("ok");
    }

    @PutMapping("project/{id}/reload")
    public ResponseEntity<String> reloadFromGit(@PathVariable String id, @RequestBody ProjectCreationRequest repo) {
        projectService.reloadProject(id, repo.getRepoUrl());
        return ResponseEntity.ok("ok");
    }

    @PutMapping("project/{id}")
    public ResponseEntity<String> updateProject(@PathVariable String id, @RequestBody ProjectChangeRequest changes) {
        projectService.updateProject(id, changes);
        return ResponseEntity.ok("ok");
    }
}
