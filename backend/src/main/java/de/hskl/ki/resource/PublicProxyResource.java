package de.hskl.ki.resource;

import de.hskl.ki.models.proxy.ProxyFormRequest;
import de.hskl.ki.services.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/proxy/")
public class PublicProxyResource {

    @Autowired
    private ProxyService proxyService;

    @PostMapping
    public ResponseEntity<?> getPage(@ModelAttribute ProxyFormRequest formRequest) {
        var output = proxyService.proxyRequest(formRequest);
        if (output.isPresent()) {
            return ResponseEntity.ok(output.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
