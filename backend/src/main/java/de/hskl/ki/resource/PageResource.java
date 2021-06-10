package de.hskl.ki.resource;

import de.hskl.ki.db.document.Page;
import de.hskl.ki.db.repository.PageRepository;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.page.PageCreationRequest;
import de.hskl.ki.services.GitService;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("page/{id}")
    public ResponseEntity<?> getPage(@PathVariable String id) {
        var found = pageRepository.findById(id);
        if (found.isPresent())
            return ResponseEntity.ok(found.get());
        return ResponseEntity.notFound().build();
    }

    @PostMapping("page")
    public ResponseEntity<?> createPage(@RequestBody PageCreationRequest page) {
        Page p = new Page(page);
        p.setSelectedProjects(
                IterableUtils.toList(projectRepository.findAllById(page.getSelectedProjects()))
        );

        pageRepository.save(p);
        return ResponseEntity.ok(p);
    }

    @DeleteMapping("page/{id}")
    public ResponseEntity<?> deletePage(@PathVariable String id) {
        var page = pageRepository.getPageById(id);
        if (page != null) {
            pageRepository.delete(page);
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("page/{id}")
    public ResponseEntity<?> updatePage(@PathVariable String id, @RequestBody PageCreationRequest page) {
        Page p = new Page(page);
        p.setId(id);
        p.setSelectedProjects(
                IterableUtils.toList(projectRepository.findAllById(page.getSelectedProjects()))
        );

        pageRepository.save(p);
        return ResponseEntity.ok("ok");
    }
}
