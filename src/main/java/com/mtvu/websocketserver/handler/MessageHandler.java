package com.mtvu.websocketserver.handler;

import com.mtvu.websocketserver.domain.message.ChatMessage;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MessageHandler implements GenericMessageHandler<ChatMessage> {

    @Inject
    @Channel("messaging-topic-out")
    Emitter<ChatMessage> messageEmitter;

    public MessageHandler() {
    }

    @Override
    public Class<ChatMessage> handleMessageType() {
        return ChatMessage.class;
    }

    @Override
    public void handleMessage(String from, ChatMessage message) {
        message.setSender(from);
        messageEmitter.send(message);
    }
}
