package de.hskl.ki.resource.websocket;

import de.hskl.ki.models.websocket.UploadCacheRequest;
import de.hskl.ki.models.websocket.UploadCacheResponse;
import de.hskl.ki.services.UploadCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class PublicUploadResource {
    private final Logger logger = LoggerFactory.getLogger(PublicUploadResource.class);

    @Autowired
    private UploadCacheService uploadCacheService;

    @MessageMapping("/upload")
    @SendTo("/topic/upload")
    public UploadCacheResponse webSocketProxyRequest(@Header("simpSessionId") String sessionId, UploadCacheRequest dto) {
        logger.info("Client with Id {} has message: {}", sessionId, dto);
        uploadCacheService.getSendCache().put(sessionId, dto);

        return new UploadCacheResponse(true, sessionId);
    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        logger.info("Client with Id {} disconnected, el remaining: {}", event.getSessionId(), uploadCacheService.getSendCache().size());
        uploadCacheService.getSendCache().remove(event.getSessionId());
    }

    @EventListener
    public void onConnectEvent(SessionConnectedEvent event) {
        logger.info("Client with username {} connected", event.getUser());
    }
}
