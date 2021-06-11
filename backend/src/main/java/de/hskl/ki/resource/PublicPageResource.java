package de.hskl.ki.resource;

import de.hskl.ki.db.document.Page;
import de.hskl.ki.db.repository.PageRepository;
import de.hskl.ki.db.repository.ProjectRepository;
import de.hskl.ki.models.page.PageCreationRequest;
import de.hskl.ki.services.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public/pa/")
public class PublicPageResource {
    @Autowired
    GitService gitService;

    @Autowired
    private PageRepository pageRepository;

    @GetMapping("page/{id}")
    public ResponseEntity<?> getPage(@PathVariable String id) {
        var found = pageRepository.findById(id);
        System.out.println(found);
        if(found.isPresent() && found.get().isActive())
            return ResponseEntity.ok(found.get());
        return ResponseEntity.notFound().build();
    }
}
