package de.hskl.ki.resource;

import de.hskl.ki.services.ContainerProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<ResponseEntity<String>> startContainer(@PathVariable String id) {
        return CompletableFuture.supplyAsync(() -> {
            String value = proxyService.startContainer(id);
            return ResponseEntity.ok(value);
        });
    }

    @DeleteMapping("container/{id}")
    public CompletableFuture<ResponseEntity<String>> stopContainer(@PathVariable String id) {
        return CompletableFuture.supplyAsync(() -> {
            String value = proxyService.stopContainer(id);
            return ResponseEntity.ok(value);
        });
    }
}
