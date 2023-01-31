package com.mtvu.websocketserver.handler.chat;

import com.mtvu.websocketserver.domain.conversation.Conversation;
import com.mtvu.websocketserver.handler.GenericMessageHandler;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConversationHandler extends GenericMessageHandler<Conversation> {

    public ConversationHandler() {
        super();
    }

    @Override
    public Class<Conversation> handleMessageType() {
        return Conversation.class;
    }

    @Override
    public void create(String from, Conversation message) {

    }

    @Override
    public void update(String from, Conversation message) {

    }

    @Override
    public String getChannel() {
        return "conversation";
    }
}
