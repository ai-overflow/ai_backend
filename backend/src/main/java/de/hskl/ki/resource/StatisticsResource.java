package de.hskl.ki.resource;

import de.hskl.ki.db.document.Statistics;
import de.hskl.ki.db.repository.StatisticsRepository;
import de.hskl.ki.models.exceptions.AIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/st/")
public class StatisticsResource {
    @Autowired
    private StatisticsRepository statisticsRepository;

    @GetMapping("project/{id}")
    public ResponseEntity<Statistics> getProject(@PathVariable String id) {
        var projectStats = statisticsRepository.findById(id, 100);
        if (projectStats.isEmpty()) {
            throw new AIException("Project not found!", StatisticsResource.class);
        }
        return projectStats
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
