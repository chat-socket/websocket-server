package com.mtvu.websocketserver.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

/**
 * @author mvu
 * @project chat-socket
 **/
public class LoggingStompSessionHandler extends StompSessionHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingStompSessionHandler.class);

    private final Type payloadType;

    public LoggingStompSessionHandler(Type payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return payloadType;
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {
        LOGGER.error("Exception occurred when handling WebSocket message.", exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        LOGGER.error("Exception occurred when transporting data.", exception);
    }
}
