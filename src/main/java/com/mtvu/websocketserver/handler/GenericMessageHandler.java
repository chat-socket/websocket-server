package com.mtvu.websocketserver.handler;

import com.mtvu.websocketserver.domain.GenericMessage;

public interface GenericMessageHandler<T extends GenericMessage> {

    Class<T> handleMessageType();

    void handleMessage(String from, T message);
}
