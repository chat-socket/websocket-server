package com.mtvu.websocketserver.handler.chat;

import com.mtvu.websocketserver.domain.message.ChatMessage;
import com.mtvu.websocketserver.handler.GenericMessageHandler;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MessageHandler extends GenericMessageHandler<ChatMessage> {

    @Inject
    @Channel("messaging-topic-out")
    Emitter<Record<String, ChatMessage>> messageEmitter;

    public MessageHandler() {
        super();
    }

    @Override
    public Class<ChatMessage> handleMessageType() {
        return ChatMessage.class;
    }

    @Override
    public void create(String from, ChatMessage message) {
        messageEmitter.send(Record.of(from, message));
    }

    @Override
    public void update(String from, ChatMessage message) {

    }

    @Override
    public String getChannel() {
        return "message";
    }
}
