package de.hskl.ki.resource.websocket;

import de.hskl.ki.models.websocket.UploadCacheRequest;
import de.hskl.ki.models.websocket.UploadCacheResponse;
import de.hskl.ki.services.ProxyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class PublicUploadResource {
    private final Logger logger = LoggerFactory.getLogger(PublicUploadResource.class);

    @MessageMapping("/chat")
    // Sends the return value of this method to /topic/messages
    @SendTo("/topic/messages")
    public UploadCacheResponse getMessages(UploadCacheRequest dto){

        return new UploadCacheResponse();

    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        logger.debug("Client with username {} disconnected", event.getUser());
    }
}
