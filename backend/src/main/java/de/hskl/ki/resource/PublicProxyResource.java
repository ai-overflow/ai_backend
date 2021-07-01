package de.hskl.ki.resource;

import de.hskl.ki.models.proxy.ProxyFormRequest;
import de.hskl.ki.services.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/public/proxy/")
public class PublicProxyResource {

    @Autowired
    private ProxyService proxyService;

    @PostMapping
    public CompletableFuture<ResponseEntity<byte[]>> getPage(@ModelAttribute ProxyFormRequest formRequest) {
        return CompletableFuture.supplyAsync(() -> {
            var output = proxyService.proxyRequest(formRequest);
            return ResponseEntity.ok(output);
        });
    }
}
