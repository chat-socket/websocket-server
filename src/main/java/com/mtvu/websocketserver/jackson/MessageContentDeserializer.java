package com.mtvu.websocketserver.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.mtvu.websocketserver.domain.message.MessageContent;
import com.mtvu.websocketserver.domain.message.MessageContentType;
import com.mtvu.websocketserver.domain.message.RecordingMessageContent;
import com.mtvu.websocketserver.domain.message.TextMessageContent;

import java.io.IOException;

public class MessageContentDeserializer extends StdDeserializer<MessageContent> {

    public MessageContentDeserializer() {
        this(MessageContent.class);
    }

    public MessageContentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MessageContent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);
        MessageContentType contentType = MessageContentType.get(node.get("type").textValue());
        if (contentType == MessageContentType.TEXT) {
            return codec.treeToValue(node, TextMessageContent.class);
        } else if (contentType == MessageContentType.RECORDING) {
            return codec.treeToValue(node, RecordingMessageContent.class);
        }
        throw new RuntimeException("Unable to decode message " + p.getValueAsString());
    }
}
