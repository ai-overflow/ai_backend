package de.hskl.ki.resource;

import de.hskl.ki.db.document.Page;
import de.hskl.ki.db.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/pa/")
public class PublicPageResource {
    @Autowired
    private PageRepository pageRepository;

    @GetMapping("page/{id}")
    public ResponseEntity<Page> getPage(@PathVariable String id) {
        var found = pageRepository.findById(id);
        if (found.isPresent() && found.get().isActive())
            return ResponseEntity.ok(found.get());
        return ResponseEntity.notFound().build();
    }
}
