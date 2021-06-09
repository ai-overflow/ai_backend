package de.hskl.ki.resource;

import de.hskl.ki.db.document.Page;
import de.hskl.ki.db.repository.PageRepository;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.page.PageCreationRequest;
import de.hskl.ki.services.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pa/")
public class PageResource {
    @Autowired
    GitService gitService;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("page")
    public List<Page> getAll() {
        return pageRepository.findAll();
    }

    @PostMapping("page")
    public ResponseEntity<?> cloneRepo(@RequestBody PageCreationRequest page) {
        Page p = new Page(page);
        p.setSelectedProjects(
                page.getSelectedProjects()
                        .stream()
                        .map(e -> projectRepository.getProjectById(e)).collect(Collectors.toList())
        );
        pageRepository.save(p);

        return ResponseEntity.ok(p);
    }

    @DeleteMapping("page/{id}")
    public ResponseEntity<?> deleteRepo(@PathVariable String id) {
        return ResponseEntity.ok("ok");
    }
}
