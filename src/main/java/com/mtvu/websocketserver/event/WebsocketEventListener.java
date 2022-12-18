package com.mtvu.websocketserver.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

/**
 * @author mvu
 * @project chat-socket
 **/
@Component
public class WebsocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketEventListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        LOGGER.info("Received a new web socket connection: " + event.toString());
    }

    @EventListener
    public void handleWebsocketConnect(SessionConnectEvent event) {
        LOGGER.info("Connection event: " + event.toString());
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        LOGGER.info("Subscribe event: " + event.toString());
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        LOGGER.info("Unsubscribe event: " + event.toString());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        LOGGER.info("Websocket connection " + event.getSessionId() + " terminated - " + event.toString());
    }
}
