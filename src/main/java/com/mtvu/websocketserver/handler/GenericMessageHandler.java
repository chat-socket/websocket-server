package com.mtvu.websocketserver.handler;

import com.mtvu.websocketserver.domain.GenericMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GenericMessageHandler<T extends GenericMessage> {

    public static Map<String, GenericMessageHandler<? extends GenericMessage>> HANDLERS = new ConcurrentHashMap<>();

    public GenericMessageHandler() {
        HANDLERS.put(getChannel(), this);
    }

    public abstract Class<T> handleMessageType();

    public abstract void handleMessage(String from, T message);

    public abstract String getChannel();
}
