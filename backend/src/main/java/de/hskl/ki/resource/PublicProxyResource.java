package de.hskl.ki.resource;

import de.hskl.ki.db.repository.PageRepository;
import de.hskl.ki.models.proxy.ProxyFormRequest;
import de.hskl.ki.services.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/proxy/")
public class PublicProxyResource {
    @Autowired
    GitService gitService;

    @Autowired
    private PageRepository pageRepository;

    @PostMapping
    public ResponseEntity<?> getPage(@ModelAttribute ProxyFormRequest formRequest) {
        System.out.println(formRequest);
        return ResponseEntity.ok("ok");
    }
}
