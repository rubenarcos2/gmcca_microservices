package com.rarcos.gmcca_notifications.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        messagingTemplate.convertAndSend("/topic/messages", "Se ha conectado un usuario");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        messagingTemplate.convertAndSend("/topic/messages", "Se ha desconectado un usuario");
    }
}