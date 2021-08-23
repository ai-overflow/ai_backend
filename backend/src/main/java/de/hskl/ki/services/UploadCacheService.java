package de.hskl.ki.services;

import de.hskl.ki.models.websocket.UploadCacheRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadCacheService {
    private final Map<String, UploadCacheRequest> sendCache = new ConcurrentHashMap<>();

    public Map<String, UploadCacheRequest> getSendCache() {
        return sendCache;
    }
}
