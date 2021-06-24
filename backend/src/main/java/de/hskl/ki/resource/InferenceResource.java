package de.hskl.ki.resource;

import de.hskl.ki.services.InferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/in/")
public class InferenceResource {
    @Autowired
    private InferenceService inferenceService;

    @PutMapping("activate/{model}")
    public ResponseEntity<String> activateModel(@PathVariable String model) {
        inferenceService.activateModel(model);
        return ResponseEntity.ok().build();
    }

    @PutMapping("deactivate/{model}")
    public ResponseEntity<String> deactivateModel(@PathVariable String model) {
        inferenceService.deactivateModel(model);
        return ResponseEntity.ok().build();
    }

    @GetMapping("status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok(inferenceService.getStatus());
    }
}
