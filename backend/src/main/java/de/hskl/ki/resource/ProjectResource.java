package de.hskl.ki.resource;

import de.hskl.ki.db.document.Project;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.git.GitCreationRequest;
import de.hskl.ki.services.GitService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<?> cloneRepo(@RequestBody GitCreationRequest repo) throws GitAPIException, IOException {
        var gitServiceResponse = gitService.generateProject(repo);
        if (gitServiceResponse.isPresent()) {
            return ResponseEntity.ok(gitServiceResponse.get());
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @DeleteMapping("project/{id}")
    public ResponseEntity<?> deleteRepo(@PathVariable String id) {
        if(gitService.deleteProject(id))
            return ResponseEntity.ok("ok");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
