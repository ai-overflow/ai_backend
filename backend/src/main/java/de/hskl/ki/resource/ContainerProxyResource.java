package de.hskl.ki.resource;

import de.hskl.ki.services.ContainerProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
@RequestMapping("/api/v1/cp/")
public class ContainerProxyResource {
    @Autowired
    private ContainerProxyService proxyService;


    @GetMapping("container")
    public List<String> getAllContainer() throws IOException {
        return proxyService.getAllContainer();
    }

    @PostMapping("container/{id}")
    public ResponseEntity<String> startContainer(@PathVariable String id) throws IOException {
        Optional<String> value = proxyService.startContainer(id);
        return value.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("container/{id}")
    public ResponseEntity<String> stopContainer(@PathVariable String id) throws IOException {
        Optional<String> value = proxyService.stopContainer(id);
        return value.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
