package de.hskl.ki.resource;

import de.hskl.ki.services.ContainerProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerAdvice
@RequestMapping("/api/v1/cp/")
public class ContainerProxyResource {
    @Autowired
    private ContainerProxyService proxyService;


    @GetMapping("container")
    public List<String> getAllContainer() {
        return proxyService.getAllContainer();
    }

    @PostMapping("container/{id}")
    public ResponseEntity<String> startContainer(@PathVariable String id) {
        String value = proxyService.startContainer(id);
        return ResponseEntity.ok(value);
    }

    @DeleteMapping("container/{id}")
    public ResponseEntity<String> stopContainer(@PathVariable String id) {
        String value = proxyService.stopContainer(id);
        return ResponseEntity.ok(value);
    }
}
