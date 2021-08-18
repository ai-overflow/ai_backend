package de.hskl.ki.resource.websocket;

import de.hskl.ki.models.websocket.UploadCacheRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
public class PublicUploadResource {
    private final Logger logger = LoggerFactory.getLogger(PublicUploadResource.class);
    private final Map<String, List<UploadCacheRequest>> sendCache = new HashMap<>();

    @MessageMapping("/upload")
    @SendTo("/topic/upload")
    public void webSocketProxyRequest(@Header("simpSessionId") String sessionId, UploadCacheRequest dto) {
        logger.info("Client with Id {} has message: {}", sessionId, dto);
        var el = sendCache.computeIfAbsent(sessionId, k -> new ArrayList<>());
        el.add(dto); // TODO: replace with real payload
    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        logger.info("Client with Id {} disconnected, el remaining: {}", event.getSessionId(), sendCache.size());
        sendCache.remove(event.getSessionId());
    }

    @EventListener
    public void onConnectEvent(SessionConnectedEvent event) {
        logger.info("Client with username {} connected", event.getUser());
    }
}
